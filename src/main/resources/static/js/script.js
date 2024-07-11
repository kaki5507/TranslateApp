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
/**
 * 텍스트를 지정된 대상 언어로 번역하는 함수입니다.
 * @param {string} targetLang - 대상 언어 코드 ('EN', 'KO', 'JA').
 */
function translateImage(targetLang) {
    // 번역할 텍스트를 해당 언어에 맞는 요소에서 가져옵니다
    const textToTranslate = document.getElementById('translatedText-EN').innerText.trim();

    // 번역 API에 번역할 텍스트와 대상 언어를 포함한 fetch 요청을 보냅니다
    fetch(`/translate?q=${textToTranslate}&targetLang=${targetLang}`)
        .then(response => {
            console.log('응답 상태:', response.status);
            // HTTP 응답이 정상적이지 않은 경우 오류를 throw합니다
            if (!response.ok) {
                throw new Error('네트워크 응답이 문제가 있습니다 ' + response.statusText);
            }
            return response.json(); // JSON 형식으로 응답을 가져옵니다
        })
        .then(data => {
            console.log('번역된 데이터:', data);
            // 대상 언어에 따라 적절한 요소에 번역된 텍스트를 설정합니다
            switch (targetLang) {
                case 'en':
                    document.getElementById('translatedText-EN').innerText = data.translatedText;
                    break;
                case 'ko':
                    document.getElementById('translatedText-KR').innerText = data.translatedText;
                    break;
                case 'ja':
                    document.getElementById('translatedText-JP').innerText = data.translatedText;
                    break;
                default:
                    break;
            }
        })
        .catch(error => {
            console.error('fetch 작업 중 문제가 발생했습니다:', error);
        });
}
