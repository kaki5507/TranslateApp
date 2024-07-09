function uploadImage() {
    const imageInput = document.getElementById('imageInput').files[0]; // input type="file"의 요소에서 첫번쨰로 선택된 파일을 말하는 것
    const formData = new FormData(); // html에서 <form> 요소 보낼 수 있게 내장 객체 동적으로 javascript에서 사용 가능
    formData.append('image', imageInput); // form이름 image로 해서 imageInput 태워서

    // fetch로 비동기로 보낸다. fetch 사용이유 promise와 함께 사용하여 간결함 , 또 별도의 라이브러리나 프레임워크 설치 필요없음 내장객체 axios는 fetch보다 더 많은 기능을 사용
    fetch('/upload', {
        method: 'POST',
        body: formData
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Error uploading image: ' + response.statusText);
            }
            return response.json();
        })
        .then(data => {
            console.log('Image uploaded successfully:', data);
        })
        .catch(error => {
            console.error('Error uploading image:', error);
        });
}

// ocr 번역 이미지 함수
/*
*   1. fetch에서 보낼 수 있는 옵션들
*    - method : 'POST' 요청 메소드
*    - header : 헤더를 설정
*    headers: {
           'Content-Type': 'application/json',
           'Authorization': 'Bearer token'
      }
      JSON 데이터 전송할때 body : JSON.stringfy({ key : 'value }) 형식으로 보낼 수 있음
      mode: 'cors' 요청 모드 기본값 'cors' 임
      credentials: 'include' 요청에 포함될 자격 증명 기본 값 'same-origin'임

      2. Promise는 작업의 완료 또는 실패를 나타내는 객체
        - Pending (대기) , Fullfilled (이행) , Rejected (거부)
      -> fetch는 promise 객체를 반환하여 현재 대기,이행,거부 인지 보여준다.

      - promise는 이런 형태
      let promise = new Promise(function(resolve, reject) {
          setTimeout(() => resolve("완료!"), 1000); // 1초 후에 "완료!"를 반환
        });

        promise.then(
          result => console.log(result), // "완료!"를 출력
          error => console.log(error) // 에러 발생 시 실행
        );

        fetch를 세밀하게 다룰 수 있음
*
* */
function translateImage() {
    fetch('/translate') // 네트워크에 요청 보냄 서버로 부터 응답 받아옴 , 파라미터값으로는 url이랑 options 넣을 수 있음
        .then(response => { // HTTP 응답의 모든 정보를 포함한다. response.ok는 상태 코드 200~299 범위 정상 받았음
            console.log('Response status:', response.status); // HTTP 상태 코드
            if (!response.ok) {
                throw new Error('Network response was not ok ' + response.statusText); // HTTP 상태 메시지 ★ 두번째 then으로 넘어감
            }
            return response.text(); // 먼저 텍스트로 받아서
        })
        .then(text => { // 첫번째는 response를 받고 두번쨰에 text를 받음 메소드에서 변환된 텍스트 처리
            try {
                const data = JSON.parse(text); // JSON 데이터를 파싱하여 Javascript 객체로 변환
                console.log('Parsed data:', data);
                document.getElementById('translatedText').innerText = data.translatedText;
            } catch (error) {
                throw new Error('Error parsing JSON: ' + error.message);
            }
        })
        .catch(error => {
            console.error('There was a problem with the fetch operation:', error);
        });
}

document.getElementById('translateButton').addEventListener('click', translateImage);