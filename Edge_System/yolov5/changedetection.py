import os
import cv2
import pathlib
import requests
from datetime import datetime

class ChangeDetection:
    HOST = 'http://127.0.0.1:8000'
    username = 'admin'
    password = '00000000'

    def __init__(self, names):
        self.result_prev = [0 for _ in range(len(names))]

        # Token 발급 (DRF authtoken)
        res = requests.post(self.HOST + '/api-token-auth/', data={
            'username': self.username,
            'password': self.password
        }, timeout=5)
        res.raise_for_status()
        j = res.json()
        if 'token' not in j:
            raise RuntimeError(f"Token not found in response: {j}")
        self.token = j['token']
        print("TOKEN:", self.token)

    def add(self, names, detected_current, save_dir, image, count_current=None, best_cls=None, best_conf=None):
        """
        best_cls  :  이번 프레임에서 대표로 업로드할 클래스 이름(문자열)
        best_conf :  그 클래스 confidence (float)
        """

        # streak 초기화
        if not hasattr(self, "streak") or len(self.streak) != len(names):
            self.streak = [0 for _ in range(len(names))]

        # streak 갱신
        for i in range(len(names)):
            self.streak[i] = self.streak[i] + 1 if detected_current[i] else 0

        # 새로 등장(0->1)한 것이 있을 때만 업로드
        change_flag = any(self.result_prev[i] == 0 and detected_current[i] == 1 for i in range(len(self.result_prev)))
        self.result_prev = detected_current[:]
        if not change_flag:
            return

        # 업로드할 waste_type/confidence 결정
        # best_cls/best_conf가 detect.py에서 넘어오면 그걸 쓰고,
        # 아니면 감지된 클래스 중 첫 번째를 사용하고 conf는 0.5로 기본값 처리
        if best_cls is None:
            current_classes = [names[i] for i in range(len(names)) if detected_current[i]]
            best_cls = current_classes[0] if current_classes else "unknown"

        if best_conf is None:
            best_conf = 0.5

        # COCO 클래스명을 분리배출 카테고리로 매핑
        waste_type = self.map_to_waste_type(best_cls)
        confidence = float(best_conf)

        self.send(save_dir, image, waste_type, confidence)

    def map_to_waste_type(self, cls_name: str) -> str:
        """
        COCO label -> 우리 분류 라벨로 매핑
        """
        if cls_name in ["bottle", "cup"]:
            return "플라스틱"
        if cls_name in ["can"]:
            return "캔"
        if cls_name in ["book"]:
            return "종이"
        return "일반"

    def send(self, save_dir, image, waste_type: str, confidence: float):
        # 저장 경로 구성
        today = datetime.now()

        base = pathlib.Path(os.getcwd()) / pathlib.Path(save_dir) / "detected" / str(today.year) / str(today.month) / str(today.day)
        base.mkdir(parents=True, exist_ok=True)

        filename = f"{today.hour}-{today.minute}-{today.second}-{today.microsecond}.jpg"
        full_path = base / filename

        # 이미지 저장
        dst = cv2.resize(image, dsize=(320, 240), interpolation=cv2.INTER_AREA)
        cv2.imwrite(str(full_path), dst)

        # DRF authtoken 헤더는 Token
        headers = {
            'Authorization': f'Token {self.token}',
            'Accept': 'application/json'
        }

        data = {
            "waste_type": waste_type,
            "confidence": confidence
        }

        with open(full_path, "rb") as f:
            files = {"image": f}
            res = requests.post(self.HOST + '/api/waste/', data=data, files=files, headers=headers, timeout=10)

        print("UPLOAD:", res.status_code, res.text)
