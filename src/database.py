import sqlite3
import os
from datetime import datetime
from typing import Optional

DB_PATH = os.path.join(os.path.dirname(__file__), "tracking.db")


def get_connection():
    conn = sqlite3.connect(DB_PATH)
    conn.row_factory = sqlite3.Row
    return conn


def init_db():
    conn = get_connection()
    conn.execute("""
        CREATE TABLE IF NOT EXISTS api_tracking_log (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            api_name TEXT NOT NULL,
            caller_name TEXT DEFAULT 'anonymous',
            person_type TEXT,
            person_level TEXT,
            person_dept TEXT,
            call_time TEXT NOT NULL,
            response_time_ms REAL,
            status TEXT DEFAULT 'success'
        )
    """)
    conn.commit()
    conn.close()


def insert_log(api_name: str, caller_name: str, person_type: Optional[str],
               person_level: Optional[str], person_dept: Optional[str],
               call_time: str, response_time_ms: float, status: str = "success"):
    conn = get_connection()
    conn.execute(
        "INSERT INTO api_tracking_log (api_name, caller_name, person_type, person_level, person_dept, call_time, response_time_ms, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
        (api_name, caller_name, person_type, person_level, person_dept, call_time, response_time_ms, status)
    )
    conn.commit()
    conn.close()


def get_overview():
    conn = get_connection()
    cursor = conn.cursor()

    # total
    cursor.execute("SELECT COUNT(*) FROM api_tracking_log")
    total = cursor.fetchone()[0]

    # by api
    cursor.execute("SELECT api_name, COUNT(*) FROM api_tracking_log GROUP BY api_name")
    by_api = {row[0]: row[1] for row in cursor.fetchall()}

    # by person_type
    cursor.execute("SELECT person_type, COUNT(*) FROM api_tracking_log GROUP BY person_type")
    by_type = [{"label": r[0] or "未知", "value": r[1]} for r in cursor.fetchall()]

    # by person_level
    cursor.execute("SELECT person_level, COUNT(*) FROM api_tracking_log GROUP BY person_level")
    by_level = [{"label": r[0] or "未知", "value": r[1]} for r in cursor.fetchall()]

    # by person_dept
    cursor.execute("SELECT person_dept, COUNT(*) FROM api_tracking_log GROUP BY person_dept")
    by_dept = [{"label": r[0] or "未知", "value": r[1]} for r in cursor.fetchall()]

    # trend by day
    cursor.execute("""
        SELECT DATE(call_time) as day, COUNT(*) as cnt
        FROM api_tracking_log
        GROUP BY DATE(call_time)
        ORDER BY day
    """)
    trend = [{"date": r[0], "count": r[1]} for r in cursor.fetchall()]

    conn.close()

    return {
        "totalCalls": total,
        "byApi": by_api,
        "byDimension": {
            "personType": by_type,
            "personLevel": by_level,
            "personDept": by_dept
        },
        "trend": trend
    }