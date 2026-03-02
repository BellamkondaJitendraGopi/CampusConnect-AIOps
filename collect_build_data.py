import requests
import csv
import os
from datetime import datetime

# ==============================
# CONFIGURATION
# ==============================
JENKINS_URL = "http://localhost:8080"
JOB_NAME = "CampusConnect-CI"
USERNAME = "adminjitendra"
API_TOKEN = "15xQsQq9mJdXBjwiuzEf7Hp7SoLwNLVgen"

DATASET_FILE = "dataset.csv"
LOG_DIR = "logs"
MAX_BUILDS = 50   # Number of recent builds to collect

os.makedirs(LOG_DIR, exist_ok=True)

# ==============================
# FETCH BUILDS LIST
# ==============================
builds_api = f"{JENKINS_URL}/job/{JOB_NAME}/api/json?tree=builds[number,result,timestamp,duration,changeSet[items[commitId,msg,author[fullName],affectedPaths]]]"
response = requests.get(builds_api, auth=(USERNAME, API_TOKEN))

if response.status_code != 200:
    print("Failed to fetch builds list")
    print("Status Code:", response.status_code)
    print("Response:", response.text)
    exit()

data = response.json()
builds = data.get("builds", [])[:MAX_BUILDS]

# ==============================
# LOAD EXISTING BUILD NUMBERS (avoid duplicates)
# ==============================
existing_builds = set()

if os.path.isfile(DATASET_FILE):
    with open(DATASET_FILE, "r") as f:
        reader = csv.DictReader(f)
        for row in reader:
            existing_builds.add(int(row["build_number"]))

# ==============================
# PREPARE CSV
# ==============================
file_exists = os.path.isfile(DATASET_FILE)
csv_file = open(DATASET_FILE, "a", newline="", encoding="utf-8")
writer = csv.writer(csv_file)

if not file_exists:
    writer.writerow([
        "build_number",
        "timestamp",
        "duration_ms",
        "result",
        "commit_id",
        "author",
        "files_changed",
        "commit_message_length",
        "log_lines",
        "error_count",
        "warning_count"
    ])

# ==============================
# PROCESS EACH BUILD
# ==============================
for build in builds:

    build_number = build["number"]

    # Skip already processed builds
    if build_number in existing_builds:
        continue

    result = build.get("result")
    duration = build.get("duration")
    timestamp = datetime.fromtimestamp(build["timestamp"] / 1000)

    change_items = build.get("changeSet", {}).get("items", [])

    if change_items:
        commit_id = change_items[0].get("commitId", "")
        author = change_items[0].get("author", {}).get("fullName", "")
        affected_paths = change_items[0].get("affectedPaths", [])
        files_changed = len(affected_paths)
        commit_message = change_items[0].get("msg", "")
        commit_message_length = len(commit_message)
    else:
        commit_id = ""
        author = ""
        files_changed = 0
        commit_message_length = 0

    # ==============================
    # FETCH CONSOLE LOG
    # ==============================
    log_url = f"{JENKINS_URL}/job/{JOB_NAME}/{build_number}/consoleText"
    log_response = requests.get(log_url, auth=(USERNAME, API_TOKEN))
    log_text = log_response.text

    log_lines = len(log_text.splitlines())
    error_count = log_text.lower().count("error")
    warning_count = log_text.lower().count("warning")

    # Save raw log
    with open(f"{LOG_DIR}/build_{build_number}.txt", "w", encoding="utf-8") as f:
        f.write(log_text)

    # ==============================
    # WRITE TO DATASET
    # ==============================
    writer.writerow([
        build_number,
        timestamp,
        duration,
        result,  # SUCCESS / FAILURE / ABORTED / etc
        commit_id,
        author,
        files_changed,
        commit_message_length,
        log_lines,
        error_count,
        warning_count
    ])

    print(f"Collected Build #{build_number} ({result})")

csv_file.close()

print("Dataset update complete.")