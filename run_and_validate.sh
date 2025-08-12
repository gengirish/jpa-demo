
#!/bin/bash
# Shell script to run and validate the Maven project and its test cases
# Project: jpa-demo

set -e  # Exit on any error

# Color codes for output formatting
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_header() {
    echo -e "\n${BLUE}================================${NC}"
    echo -e "${BLUE} $1${NC}"
    echo -e "${BLUE}================================${NC}\n"
}

# Function to check if Maven is installed
check_maven() {
    print_status "Checking Maven installation..."
    if ! command -v mvn &> /dev/null; then
        print_error "Maven is not installed or not in PATH"
        print_error "Please install Maven and ensure it's in your PATH"
        exit 1
    fi
    
    mvn_version=$(mvn -version | head -n 1)
    print_success "Maven found: $mvn_version"
}

# Function to check Java version
check_java() {
    print_status "Checking Java installation..."
    if ! command -v java &> /dev/null; then
        print_error "Java is not installed or not in PATH"
        exit 1
    fi
    
    java_version=$(java -version 2>&1 | head -n 1)
    print_success "Java found: $java_version"
}

# Function to validate project structure
validate_project_structure() {
    print_status "Validating project structure..."
    


    required_files=(
        "pom.xml"
        "src/main/java/com/example/jpademo/JpaDemoApplication.java"
        "src/test/java/com/example/jpademo/ProductServiceTest.java"
    )

    for file in "${required_files[@]}"; do
        if [[ ! -f "$file" ]]; then
            print_error "Required file missing: $file"
            exit 1
        fi
    done

    print_success "Project structure validation passed"
}

# Function to clean the project
clean_project() {
    print_status "Cleaning project..."
    if mvn clean > /dev/null 2>&1; then
        print_success "Project cleaned successfully"
    else
        print_error "Failed to clean project"
        exit 1
    fi
}

# Function to compile the project
compile_project() {
    print_status "Compiling project..."
    if mvn compile -q; then
        print_success "Project compiled successfully"
    else
        print_error "Compilation failed"
        exit 1
    fi
}

# Function to compile test sources
compile_tests() {
    print_status "Compiling test sources..."
    if mvn test-compile -q; then
        print_success "Test sources compiled successfully"
    else
        print_error "Test compilation failed"
        exit 1
    fi
}

# Function to run tests
run_tests() {
    print_status "Running tests..."
    

    # Run tests and capture output
    if mvn test -q > test_output.log 2>&1; then
        print_success "All tests passed"

        # Extract test results from Surefire reports
        for report in target/surefire-reports/TEST-com.example.jpademo.ProductServiceTest.xml; do
            if [[ -f "$report" ]]; then
                test_count=$(grep -o 'tests="[0-9]*"' "$report" | grep -o '[0-9]*')
                failures=$(grep -o 'failures="[0-9]*"' "$report" | grep -o '[0-9]*')
                errors=$(grep -o 'errors="[0-9]*"' "$report" | grep -o '[0-9]*')
                print_success "Test Results ($report): $test_count tests run, $failures failures, $errors errors"
            fi
        done
    else
        print_error "Tests failed"
        echo "Test output:"
        cat test_output.log
        exit 1
    fi
}

# Function to validate test coverage
validate_test_coverage() {
    print_status "Validating test coverage..."
    

    # Check if test classes exist
    if [[ -f "target/test-classes/com/example/jpademo/ProductServiceTest.class" ]]; then
        print_success "Test classes found and compiled"
    else
        print_warning "Test classes not found in expected location"
    fi

    # Check if main classes exist
    if [[ -f "target/classes/com/example/jpademo/JpaDemoApplication.class" ]]; then
        print_success "Main classes found and compiled"
    else
        print_error "Main classes not found"
        exit 1
    fi
}

# Function to run dependency check
check_dependencies() {
    print_status "Checking project dependencies..."
    
    if mvn dependency:resolve -q > /dev/null 2>&1; then
        print_success "All dependencies resolved successfully"
    else
        print_error "Failed to resolve dependencies"
        exit 1
    fi
}


# Function to validate specific test categories
validate_test_categories() {
    print_status "Validating test categories..."

    categories=(
        "ProductServiceTest"
    )

    for category in "${categories[@]}"; do
        if mvn test -Dtest="$category" -q > /dev/null 2>&1; then
            print_success "Test category '$category' passed"
        else
            print_error "Test category '$category' failed"
            exit 1
        fi
    done
}

# Function to generate project report
generate_report() {
    print_status "Generating project report..."
    

    echo "Project Validation Report" > validation_report.txt
    echo "=========================" >> validation_report.txt
    echo "Date: $(date)" >> validation_report.txt
    echo "Project: jpa-demo" >> validation_report.txt
    echo "" >> validation_report.txt

    echo "Maven Version:" >> validation_report.txt
    mvn -version >> validation_report.txt 2>&1
    echo "" >> validation_report.txt

    echo "Java Version:" >> validation_report.txt
    java -version >> validation_report.txt 2>&1
    echo "" >> validation_report.txt

    echo "Dependencies:" >> validation_report.txt
    mvn dependency:list -q >> validation_report.txt 2>&1
    echo "" >> validation_report.txt

    for report in target/surefire-reports/TEST-com.example.jpademo.ProductServiceTest.xml; do
        if [[ -f "$report" ]]; then
            echo "Test Results Summary ($report):" >> validation_report.txt
            grep -E "(tests=|failures=|errors=|time=)" "$report" >> validation_report.txt
        fi
    done

    print_success "Report generated: validation_report.txt"
}

# Function to cleanup temporary files
cleanup() {
    print_status "Cleaning up temporary files..."
    rm -f test_output.log
    print_success "Cleanup completed"
}

# Main execution function

main() {
    print_header "Maven Project Validation Script"
    print_status "Starting validation for jpa-demo project..."

    # Pre-flight checks
    check_java
    check_maven
    validate_project_structure

    print_header "Building and Testing Project"

    # Build and test
    clean_project
    check_dependencies
    compile_project
    compile_tests
    validate_test_coverage
    run_tests
    validate_test_categories

    print_header "Generating Report"
    generate_report

    print_header "Validation Complete"
    print_success "All validations passed successfully!"
    print_success "The jpa-demo project is working correctly."

    cleanup
}

# Trap to ensure cleanup on exit
trap cleanup EXIT

# Run main function
main "$@"
