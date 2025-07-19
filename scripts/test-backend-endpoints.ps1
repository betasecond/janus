# -----------------------------------------------------------------------------
# Test Backend Endpoints
#
# Description:
# This script tests which API endpoints defined in the frontend's mock server
# are implemented in the actual backend. It reads the endpoint paths from
# the frontend's 'index.cjs' file and sends requests to the running backend
# service to check their status.
#
# Usage:
# 1. Ensure your Janus backend Spring Boot application is running.
# 2. Run this script from the project root directory using PowerShell:
#    .\scripts\test-backend-endpoints.ps1
#
# The script will output the status of each endpoint.
# -----------------------------------------------------------------------------

# --- Configuration ---
# Your backend server's base URL.
# Fetched from 'application-dev.yml', default is 9901.
$backendUrl = "http://localhost:9901"

# Path to the frontend mock server definition file.
$frontendMockServerFile = "\janus-eye\server\index.cjs"

# --- Script ---
Write-Host "🚀 Starting API endpoint test for Janus backend..."
Write-Host "Targeting backend at: $backendUrl"
Write-Host "Reading endpoints from: $frontendMockServerFile"
Write-Host "--------------------------------------------------"

# Read the content of the mock server file
$fileContent = Get-Content $frontendMockServerFile -Raw

# Regex to find all app.get('/api/...') paths
$regex = [regex] "app\.get\('(/api/.*?)'"
$matches = $regex.Matches($fileContent)

if ($matches.Count -eq 0) {
    Write-Host "⚠️ No API endpoints found in '$frontendMockServerFile'. Please check the file and regex."
    exit
}

Write-Host "Found $($matches.Count) endpoints to test."
Write-Host ""

foreach ($match in $matches) {
    $endpoint = $match.Groups[1].Value.Trim()

    $uriBuilder = [System.UriBuilder]$backendUrl
    $uriBuilder.Path = $endpoint

    $appendedInfo = ""

    # Handle endpoints that require parameters for testing
    if ($endpoint -eq "/api/resources") {
        # We use a hardcoded test user ID from the V6 migration script.
        $testUserId = "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11" # Teacher Zhang's ID
        $uriBuilder.Query = "currentUserId=$testUserId"
        $appendedInfo = " (with test user)"
    }

    $requestUrl = $uriBuilder.ToString()

    Write-Host -NoNewline "Testing endpoint: $endpoint$appendedInfo ..."

    try {
        # Invoke the web request and get the status code
        $response = Invoke-WebRequest -Uri $requestUrl -Method Get -UseBasicParsing -ErrorAction Stop
        $statusCode = $response.StatusCode

        if ($statusCode -eq 200) {
            Write-Host " ✅ OK (Status: $statusCode)" -ForegroundColor Green
        } else {
            Write-Host " ⚠️ WARN (Status: $statusCode)" -ForegroundColor Yellow
        }
    } catch {
        # Handle exceptions, typically for 4xx/5xx status codes
        if ($_.Exception.Response) {
            $statusCode = [int]$_.Exception.Response.StatusCode
            Write-Host " ❌ NOT IMPLEMENTED (Status: $statusCode)" -ForegroundColor Red
        } else {
            Write-Host " 🚫 FAILED (Connection error: $($_.Exception.Message))" -ForegroundColor DarkRed
        }
    }
}

Write-Host "--------------------------------------------------"
Write-Host "✅ Test finished."
Write-Host "Endpoints marked as 'NOT IMPLEMENTED' (404) are likely missing in your backend."
