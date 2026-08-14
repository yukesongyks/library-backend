from pydantic import BaseModel
from typing import Optional, List


class ApiResponse(BaseModel):
    code: int = 200
    message: str = "success"
    data: object = None


class HelloResult(BaseModel):
    greeting: str
    timestamp: str


class HashRequest(BaseModel):
    input: str
    algorithm: str = "SHA-256"


class HashResult(BaseModel):
    input: str
    algorithm: str
    hashResult: str


class SortRequest(BaseModel):
    numbers: List[int]
    order: str = "asc"


class SortResult(BaseModel):
    originalArray: List[int]
    sortedArray: List[int]
    order: str
    swapCount: int
    executionTimeMs: float