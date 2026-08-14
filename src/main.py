import time
import csv
import io
import json
from datetime import datetime
from typing import Optional

from fastapi import FastAPI, Request, Query
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import StreamingResponse

from models import ApiResponse, HashRequest, SortRequest
from services import get_hello, compute_hash, bubble_sort
from database import init_db, insert_log, get_overview

app = FastAPI(title="Library Backend API")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


@app.on_event("startup")
def startup():
    init_db()


# --- Tracking Middleware ---
@app.middleware("http")
async def tracking_middleware(request: Request, call_next):
    start = time.time()
    response = await call_next(request)
    elapsed = (time.time() - start) * 1000

    # Only track /api/ routes
    path = request.url.path
    if not path.startswith("/api/"):
        return response

    # Extract api name
    api_name = path.split("/api/")[-1].split("/")[0].split("?")[0]

    # Extract tracking headers
    caller_name = request.headers.get("x-caller-name", "anonymous")
    person_type = request.headers.get("x-person-type")
    person_level = request.headers.get("x-person-level")
    person_dept = request.headers.get("x-person-dept")

    status = "success" if response.status_code < 400 else "error"

    insert_log(
        api_name=api_name,
        caller_name=caller_name,
        person_type=person_type,
        person_level=person_level,
        person_dept=person_dept,
        call_time=datetime.now().isoformat(),
        response_time_ms=round(elapsed, 2),
        status=status
    )

    return response


# --- Routes ---

@app.get("/api/hello")
def hello():
    return ApiResponse(data=get_hello().model_dump())


@app.post("/api/hash")
def hash_endpoint(req: HashRequest):
    result = compute_hash(req.input, req.algorithm)
    return ApiResponse(data=result.model_dump())


@app.post("/api/sort")
def sort_endpoint(req: SortRequest):
    result = bubble_sort(req.numbers, req.order)
    return ApiResponse(data=result.model_dump())


@app.get("/api/export")
def export_endpoint(
    type: str = Query(...),
    format: str = Query("json"),
    input: Optional[str] = Query(None),
    numbers: Optional[str] = Query(None),
    order: Optional[str] = Query("asc")
):
    if type == "hello":
        data = get_hello().model_dump()
    elif type == "hash":
        input_str = input or "demo-input"
        data = compute_hash(input_str, "SHA-256").model_dump()
    elif type == "sort":
        nums = [int(x) for x in (numbers or "3,1,4,1,5,9,2,6").split(",")]
        data = bubble_sort(nums, order).model_dump()
    else:
        return ApiResponse(code=400, message=f"Invalid type: {type}")

    if format == "csv":
        output = io.StringIO()
        writer = csv.writer(output)
        if isinstance(data, dict):
            writer.writerow(data.keys())
            writer.writerow(data.values())
        output.seek(0)
        return StreamingResponse(
            iter([output.getvalue()]),
            media_type="text/csv",
            headers={"Content-Disposition": f"attachment; filename={type}-result.csv"}
        )
    else:
        return StreamingResponse(
            iter([json.dumps(data, ensure_ascii=False, indent=2)]),
            media_type="application/json",
            headers={"Content-Disposition": f"attachment; filename={type}-result.json"}
        )


@app.get("/api/stats/overview")
def stats_overview():
    data = get_overview()
    return ApiResponse(data=data)


if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8080)