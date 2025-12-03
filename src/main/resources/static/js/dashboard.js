// CASA - Dashboard JavaScript

const API_BASE_URL = 'http://localhost:8080/api/v1';

let currentPage = 0;
let currentSize = 10;
let currentFilter = {};

// Load dashboard on page load
document.addEventListener('DOMContentLoaded', () => {
    loadDashboard();
});

// Load Dashboard Data
async function loadDashboard() {
    await loadStatistics();
    await loadLogs();
}

// Load Statistics
async function loadStatistics() {
    try {
        const response = await fetch(`${API_BASE_URL}/logs/statistics`);
        const stats = await response.json();

        document.getElementById('totalUploads').textContent = stats.totalUploads;
        document.getElementById('allowedUploads').textContent = stats.allowedUploads;
        document.getElementById('blockedUploads').textContent = stats.blockedUploads;
        document.getElementById('quarantinedUploads').textContent = stats.quarantinedUploads;
    } catch (error) {
        console.error('Error loading statistics:', error);
    }
}

// Load Logs
async function loadLogs(page = 0) {
    currentPage = page;

    try {
        let url = `${API_BASE_URL}/logs?page=${page}&size=${currentSize}`;

        // Apply filters
        if (currentFilter.status) {
            url = `${API_BASE_URL}/logs/status/${currentFilter.status}?page=${page}&size=${currentSize}`;
        } else if (currentFilter.userId) {
            url = `${API_BASE_URL}/logs/user/${currentFilter.userId}?page=${page}&size=${currentSize}`;
        }

        const response = await fetch(url);
        const data = await response.json();

        displayLogs(data.content);
        displayPagination(data);
    } catch (error) {
        console.error('Error loading logs:', error);
        document.getElementById('logsTableBody').innerHTML = `
            <tr>
                <td colspan="7" class="text-center text-danger">
                    <i class="fas fa-exclamation-circle me-2"></i>Error loading logs
                </td>
            </tr>
        `;
    }
}

// Display Logs in Table
function displayLogs(logs) {
    const tbody = document.getElementById('logsTableBody');

    if (logs.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="7" class="text-center text-muted">
                    <i class="fas fa-inbox me-2"></i>No logs found
                </td>
            </tr>
        `;
        return;
    }

    tbody.innerHTML = logs.map(log => `
        <tr>
            <td>${formatTimestamp(log.timestamp)}</td>
            <td>
                <i class="fas fa-file me-2"></i>${log.fileName}
                <br><small class="text-muted">${formatFileSize(log.fileSize)}</small>
            </td>
            <td>${log.userId}</td>
            <td>${getStatusBadge(log.status)}</td>
            <td>${getRiskBadge(log.riskScore)}</td>
            <td>
                ${log.sensitiveDataFound ?
            `<span class="badge bg-danger">Yes</span>
                     <br><small>${log.sensitiveTypes.join(', ')}</small>` :
            '<span class="badge bg-success">No</span>'}
            </td>
            <td>${getActionBadge(log.policyAction)}</td>
        </tr>
    `).join('');
}

// Display Pagination
function displayPagination(data) {
    const pagination = document.getElementById('pagination');

    if (data.totalPages <= 1) {
        pagination.innerHTML = '';
        return;
    }

    let html = '';

    // Previous button
    html += `
        <li class="page-item ${data.first ? 'disabled' : ''}">
            <a class="page-link" href="#" onclick="loadLogs(${currentPage - 1}); return false;">
                <i class="fas fa-chevron-left"></i>
            </a>
        </li>
    `;

    // Page numbers
    for (let i = 0; i < data.totalPages; i++) {
        if (i === 0 || i === data.totalPages - 1 || (i >= currentPage - 2 && i <= currentPage + 2)) {
            html += `
                <li class="page-item ${i === currentPage ? 'active' : ''}">
                    <a class="page-link" href="#" onclick="loadLogs(${i}); return false;">${i + 1}</a>
                </li>
            `;
        } else if (i === currentPage - 3 || i === currentPage + 3) {
            html += '<li class="page-item disabled"><span class="page-link">...</span></li>';
        }
    }

    // Next button
    html += `
        <li class="page-item ${data.last ? 'disabled' : ''}">
            <a class="page-link" href="#" onclick="loadLogs(${currentPage + 1}); return false;">
                <i class="fas fa-chevron-right"></i>
            </a>
        </li>
    `;

    pagination.innerHTML = html;
}

// Apply Filters
function applyFilters() {
    const status = document.getElementById('statusFilter').value;
    const userId = document.getElementById('userFilter').value.trim();
    const riskThreshold = document.getElementById('riskFilter').value;

    currentFilter = {};

    if (status) {
        currentFilter.status = status;
    }

    if (userId) {
        currentFilter.userId = userId;
    }

    if (riskThreshold) {
        currentFilter.riskThreshold = parseInt(riskThreshold);
    }

    loadLogs(0);
}

// Helper Functions
function getStatusBadge(status) {
    const badges = {
        'ALLOWED': '<span class="badge bg-success"><i class="fas fa-check me-1"></i>Allowed</span>',
        'BLOCKED': '<span class="badge bg-danger"><i class="fas fa-ban me-1"></i>Blocked</span>',
        'QUARANTINED': '<span class="badge bg-warning"><i class="fas fa-exclamation-triangle me-1"></i>Quarantined</span>',
        'ERROR': '<span class="badge bg-secondary"><i class="fas fa-times me-1"></i>Error</span>'
    };
    return badges[status] || status;
}

function getRiskBadge(score) {
    let badgeClass = 'bg-success';
    let icon = 'fa-check-circle';

    if (score >= 70) {
        badgeClass = 'bg-danger';
        icon = 'fa-exclamation-circle';
    } else if (score >= 40) {
        badgeClass = 'bg-warning';
        icon = 'fa-exclamation-triangle';
    }

    return `<span class="badge ${badgeClass}"><i class="fas ${icon} me-1"></i>${score}</span>`;
}

function getActionBadge(action) {
    const badges = {
        'UPLOAD': '<span class="badge bg-success">Upload</span>',
        'BLOCK': '<span class="badge bg-danger">Block</span>',
        'QUARANTINE': '<span class="badge bg-warning">Quarantine</span>',
        'ALERT': '<span class="badge bg-info">Alert</span>'
    };
    return badges[action] || action;
}

function formatTimestamp(timestamp) {
    const date = new Date(timestamp);
    return date.toLocaleString();
}

function formatFileSize(bytes) {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i];
}
