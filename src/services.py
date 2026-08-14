import hashlib
import time
from datetime import datetime
from typing import List, Optional

from models import HelloResult, HashResult, SortResult


def get_hello() -> HelloResult:
    return HelloResult(
        greeting="Hello World! Welcome to Library System",
        timestamp=datetime.now().isoformat()
    )


def compute_hash(input_str: str, algorithm: str = "SHA-256") -> HashResult:
    alg = algorithm or "SHA-256"
    h = hashlib.new(alg.replace("-", "").lower())
    h.update(input_str.encode("utf-8"))
    return HashResult(
        input=input_str,
        algorithm=alg,
        hashResult=h.hexdigest()
    )


def bubble_sort(numbers: List[int], order: str = "asc") -> SortResult:
    arr = list(numbers)
    n = len(arr)
    swap_count = 0
    start = time.time()

    ascending = order != "desc"
    for i in range(n - 1):
        for j in range(n - 1 - i):
            need_swap = arr[j] > arr[j + 1] if ascending else arr[j] < arr[j + 1]
            if need_swap:
                arr[j], arr[j + 1] = arr[j + 1], arr[j]
                swap_count += 1

    elapsed = (time.time() - start) * 1000

    return SortResult(
        originalArray=numbers,
        sortedArray=arr,
        order="asc" if ascending else "desc",
        swapCount=swap_count,
        executionTimeMs=round(elapsed, 2)
    )