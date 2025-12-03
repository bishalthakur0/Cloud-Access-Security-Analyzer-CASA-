// CASA - File Upload JavaScript

const API_BASE_URL = 'http://localhost:8080/api/v1';

// DOM Elements
const dropZone = document.getElementById('dropZone');
const fileInput = document.getElementById('fileInput');
const browseBtn = document.getElementById('browseBtn');
const uploadBtn = document.getElementById('uploadBtn');
const removeFileBtn = document.getElementById('removeFile');
const fileInfo = document.getElementById('fileInfo');
const fileName = document.getElementById('fileName');
const fileSize = document.getElementById('fileSize');
const progressContainer = document.getElementById('progressContainer');
const progressBar = document.getElementById('progressBar');
const progressText = document.getElementById('progressText');
const resultContainer = document.getElementById('resultContainer');
const userIdInput = document.getElementById('userId');

let selectedFile = null;

// Event Listeners
browseBtn.addEventListener('click', () => fileInput.click());
fileInput.addEventListener('change', handleFileSelect);
removeFileBtn.addEventListener('click', clearFile);
uploadBtn.addEventListener('click', uploadFile);

// Drag and Drop
dropZone.addEventListener('dragover', (e) => {
    e.preventDefault();
    dropZone.classList.add('drag-over');
});

dropZone.addEventListener('dragleave', () => {
    dropZone.classList.remove('drag-over');
});

dropZone.addEventListener('drop', (e) => {
    e.preventDefault();
    dropZone.classList.remove('drag-over');

    const files = e.dataTransfer.files;
    if (files.length > 0) {
        handleFile(files[0]);
    }
});

dropZone.addEventListener('click', (e) => {
    if (e.target === dropZone || e.target.closest('.drop-zone')) {
        fileInput.click();
    }
});

// Handle File Selection
function handleFileSelect(e) {
    const files = e.target.files;
    if (files.length > 0) {
        handleFile(files[0]);
    }
}

function handleFile(file) {
    selectedFile = file;

    // Display file info
    fileName.textContent = file.name;
    fileSize.textContent = formatFileSize(file.size);
    fileInfo.style.display = 'block';
    uploadBtn.disabled = false;

    // Hide result if exists
    resultContainer.style.display = 'none';
}

function clearFile() {
    selectedFile = null;
    fileInput.value = '';
    fileInfo.style.display = 'none';
    uploadBtn.disabled = true;
    resultContainer.style.display = 'none';
}

// Upload File
async function uploadFile() {
    if (!selectedFile) {
        showError('Please select a file');
        return;
    }

    // Prepare form data
    const formData = new FormData();
    formData.append('file', selectedFile);

    const userId = userIdInput.value.trim();
    if (userId) {
        formData.append('userId', userId);
    }

    // Show progress
    progressContainer.style.display = 'block';
    progressBar.style.width = '0%';
    progressText.textContent = 'Uploading and scanning...';
    uploadBtn.disabled = true;
    resultContainer.style.display = 'none';

    try {
        // Simulate progress
        let progress = 0;
        const progressInterval = setInterval(() => {
            progress += 10;
            if (progress <= 90) {
                progressBar.style.width = progress + '%';
            }
        }, 200);

        // Upload file
        const response = await fetch(`${API_BASE_URL}/upload`, {
            method: 'POST',
            body: formData
        });

        clearInterval(progressInterval);
        progressBar.style.width = '100%';

        const result = await response.json();

        // Hide progress
        setTimeout(() => {
            progressContainer.style.display = 'none';
            displayResult(result, response.status);
        }, 500);

    } catch (error) {
        console.error('Upload error:', error);
        progressContainer.style.display = 'none';
        showError('Failed to upload file. Please try again.');
    } finally {
        uploadBtn.disabled = false;
    }
}

// Display Result
function displayResult(result, statusCode) {
    resultContainer.innerHTML = '';
    resultContainer.style.display = 'block';
    resultContainer.classList.add('fade-in');

    if (result.status === 'ALLOWED') {
        resultContainer.innerHTML = `
            <div class="alert alert-success">
                <h4><i class="fas fa-check-circle me-2"></i>Upload Successful!</h4>
                <hr>
                <div class="row mt-3">
                    <div class="col-md-6">
                        <p><strong>File ID:</strong> ${result.fileId}</p>
                        <p><strong>File Name:</strong> ${result.fileName}</p>
                        <p><strong>Risk Score:</strong> <span class="badge bg-success">${result.riskScore}</span></p>
                    </div>
                    <div class="col-md-6">
                        <p><strong>Sensitive Data:</strong> ${result.sensitiveDataFound ? 'Yes' : 'No'}</p>
                        <p><strong>Action:</strong> <span class="badge bg-success">${result.policyAction}</span></p>
                        <p><strong>Storage:</strong> ${result.storageLocation || 'N/A'}</p>
                    </div>
                </div>
                ${result.sensitiveTypes && result.sensitiveTypes.length > 0 ? `
                    <div class="mt-3">
                        <strong>Detected Types:</strong>
                        ${result.sensitiveTypes.map(type => `<span class="badge bg-warning me-1">${type}</span>`).join('')}
                    </div>
                ` : ''}
            </div>
        `;
    } else if (result.status === 'BLOCKED') {
        resultContainer.innerHTML = `
            <div class="alert alert-danger">
                <h4><i class="fas fa-ban me-2"></i>Upload Blocked!</h4>
                <hr>
                <div class="row mt-3">
                    <div class="col-md-6">
                        <p><strong>File ID:</strong> ${result.fileId}</p>
                        <p><strong>File Name:</strong> ${result.fileName}</p>
                        <p><strong>Risk Score:</strong> <span class="badge bg-danger">${result.riskScore}</span></p>
                    </div>
                    <div class="col-md-6">
                        <p><strong>Sensitive Data:</strong> ${result.sensitiveDataFound ? 'Yes' : 'No'}</p>
                        <p><strong>Action:</strong> <span class="badge bg-danger">${result.policyAction}</span></p>
                        <p><strong>Reason:</strong> ${result.reason}</p>
                    </div>
                </div>
                ${result.sensitiveTypes && result.sensitiveTypes.length > 0 ? `
                    <div class="mt-3">
                        <strong>Detected Sensitive Data:</strong><br>
                        ${result.sensitiveTypes.map(type => `<span class="badge bg-danger me-1">${type}</span>`).join('')}
                    </div>
                ` : ''}
                <div class="alert alert-warning mt-3 mb-0">
                    <i class="fas fa-exclamation-triangle me-2"></i>
                    This file has been blocked due to policy violations. Please remove sensitive data and try again.
                </div>
            </div>
        `;
    } else {
        resultContainer.innerHTML = `
            <div class="alert alert-danger">
                <h4><i class="fas fa-exclamation-circle me-2"></i>Error</h4>
                <p>${result.reason || result.message || 'An error occurred during upload'}</p>
            </div>
        `;
    }
}

// Show Error
function showError(message) {
    resultContainer.innerHTML = `
        <div class="alert alert-danger">
            <i class="fas fa-exclamation-circle me-2"></i>${message}
        </div>
    `;
    resultContainer.style.display = 'block';
}

// Format File Size
function formatFileSize(bytes) {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i];
}
