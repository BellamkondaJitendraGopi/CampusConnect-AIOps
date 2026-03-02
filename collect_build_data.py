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
API_TOKEN = "11783b4ba9f27de518ba1fd9be02f4d1f5"

# ==============================
# FETCH BUILD METADATA
# ==============================
build_api = f"{JENKINS_URL}/job/{JOB_NAME}/lastBuild/api/json"
response = requests.get(build_api, auth=(USERNAME, API_TOKEN))
#data = response.json()
#i am temporarily replacing this 19 line with the following three lines

print("Status Code:", response.status_code)
print("Response Text:", response.text)
exit()

build_number = data["number"]
result = data["result"]
duration = data["duration"]
timestamp = datetime.fromtimestamp(data["timestamp"]/1000)

# ==============================
# EXTRACT COMMIT INFO
# ==============================
change_items = data.get("changeSet", {}).get("items", [])

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
log_url = f"{JENKINS_URL}/job/{JOB_NAME}/lastBuild/consoleText"
log_response = requests.get(log_url, auth=(USERNAME, API_TOKEN))
log_text = log_response.text

log_lines = len(log_text.splitlines())
error_count = log_text.lower().count("error")
warning_count = log_text.lower().count("warning")

# Save raw log
os.makedirs("logs", exist_ok=True)
with open(f"logs/build_{build_number}.txt", "w", encoding="utf-8") as f:
    f.write(log_text)

# ==============================
# APPEND TO CSV DATASET
# ==============================
file_exists = os.path.isfile("dataset.csv")

with open("dataset.csv", "a", newline="") as f:
    writer = csv.writer(f)

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

    writer.writerow([
        build_number,
        timestamp,
        duration,
        result,
        commit_id,
        author,
        files_changed,
        commit_message_length,
        log_lines,
        error_count,
        warning_count
    ])

print(f"Build #{build_number} data collected successfully.")