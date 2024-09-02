param (
    [Parameter(ValueFromRemainingArguments = $true)]
    [string[]]$command
)

# Load environment variables from .env file
Get-Content .env | ForEach-Object {
    if ($_ -match "^\s*([^#][^=]+)=(.*)$") {
        $name = $matches[1]
        $value = $matches[2]
        [System.Environment]::SetEnvironmentVariable($name, $value, [System.EnvironmentVariableTarget]::Process)
    }
}

# Concatenate the command arguments
$fullCommand = $command -join " "

# Execute the desired command
Invoke-Expression $fullCommand
