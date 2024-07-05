function uploadImage() {
    const imageInput = document.getElementById('imageInput').files[0];
    const formData = new FormData();
    formData.append('image', imageInput);

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

function translateImage() {
    fetch('/translate')
        .then(response => {
            console.log('Response status:', response.status);
            if (!response.ok) {
                throw new Error('Network response was not ok ' + response.statusText);
            }
            return response.text(); // 먼저 텍스트로 받아서
        })
        .then(text => {
            console.log('Response text:', text); // 응답 텍스트를 출력하여 확인
            try {
                const data = JSON.parse(text); // 수동으로 JSON 파싱
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