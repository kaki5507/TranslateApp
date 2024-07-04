function uploadImage() {
    const imageInput = document.getElementById('imageInput').files[0];
    const formData = new FormData();
    formData.append('image', imageInput);

    fetch('/upload', {
        method: 'POST',
        body: formData
    })
        .then(response => response.json())
        .catch(error => console.error('Error uploading image:', error));
}

function translateImage() {
    fetch('/translate')
        .then(response => response.json())
        .then(data => {
            console.log('Translated text:', data.translatedText);
            document.getElementById('translatedText').textContent = data.translatedText;
        })
        .catch(error => console.error('Error translating image:', error));
}
