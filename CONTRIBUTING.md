# Contributing to CASA

Thank you for your interest in contributing to CASA (Cloud Access Security Analyzer)!

## How to Contribute

### Reporting Bugs
1. Check if the bug has already been reported in Issues
2. Create a new issue with:
   - Clear title and description
   - Steps to reproduce
   - Expected vs actual behavior
   - System information (OS, Java version)

### Suggesting Features
1. Check existing feature requests
2. Create a new issue with:
   - Clear use case
   - Proposed solution
   - Alternative approaches considered

### Pull Requests
1. Fork the repository
2. Create a feature branch: `git checkout -b feature/your-feature-name`
3. Make your changes
4. Add tests for new functionality
5. Ensure all tests pass: `mvn test`
6. Update documentation if needed
7. Commit with clear messages
8. Push to your fork
9. Submit a pull request

## Development Setup

```bash
# Clone your fork
git clone https://github.com/YOUR_USERNAME/CASA-Cloud-Security-Analyzer.git

# Navigate to directory
cd CASA-Cloud-Security-Analyzer

# Build project
mvn clean install

# Run tests
mvn test

# Run application
mvn spring-boot:run
```

## Code Style

### Java
- Follow standard Java conventions
- Use meaningful variable names
- Add JavaDoc for public methods
- Keep methods focused and small

### Testing
- Write unit tests for new features
- Maintain 80%+ code coverage
- Use descriptive test names
- Follow AAA pattern (Arrange, Act, Assert)

### Commits
- Use present tense ("Add feature" not "Added feature")
- Keep first line under 50 characters
- Reference issues when applicable

## Code Review Process

1. All PRs require review before merging
2. Address review comments promptly
3. Keep PRs focused on single feature/fix
4. Update PR based on feedback

## Questions?

Feel free to create an issue for any questions about contributing!

---

**Thank you for contributing to CASA!** 🎉
