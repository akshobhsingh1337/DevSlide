# DevSlide

DevSlide is a presentation tool designed for developers to create and deliver code-focused presentations.

## Features

- Create and edit slides with text, images, and code snippets
- Syntax highlighting for various programming languages
- Live code editing during presentations
- Support for UML diagrams using PlantUML
- Drawing tools for creating custom graphics
- Chart creation (Bar and Pie charts)
- Presentation mode with fullscreen support
- Slide reordering and management
- Theme customization

## Prerequisites

- Java Development Kit (JDK) 17 or later
- Apache Maven

## Getting Started

### Installation

1. Clone the repository:
   ```bash
   git clone http://scc-source.lancs.ac.uk/scc210-2023-24/scc210-2324-grp-51.git
   cd scc210-2324-grp-51
   ```

2. Build the project:
   ```bash
   mvn clean install
   ```

### Running DevSlide

You can run the application using one of the following methods:

1. Using Maven:
   ```bash
   mvn exec:java
   ```

2. Using the generated JAR file:
   ```bash
   java -jar target/presenter-1.0.jar
   ```

## Usage

1. Launch DevSlide
2. On the welcome screen, you can access the Help section to find manuals, instructions, and getting started files
3. Create a new presentation or open an existing one
4. Add slides and content using the toolbar options
5. Use the preview pane to navigate between slides
6. Enter presentation mode to deliver your presentation

## Development

This project uses Maven for dependency management and build automation. For more information on using Maven, please refer to the [Maven Documentation](https://maven.apache.org/guides/getting-started/index.html).

## Project Structure

The main application code is located in `src/main/java/org/group51/`. Key classes include:

- `App.java`: Entry point of the application
- `UI.java`: Main controller for UI functions
- `PresentationFrame.java`: Main application window
- `Slide.java`: Represents a single slide
- `Presentation.java`: Manages the entire presentation

For a detailed overview of the project structure and dependencies, please refer to the `pom.xml` file in the root directory.

## License

This project is licensed under the [MIT License](LICENSE).

## Acknowledgments

- [FlatLaf](https://www.formdev.com/flatlaf/) for the modern look and feel
- [RSyntaxTextArea](https://github.com/bobbylight/RSyntaxTextArea) for syntax highlighting
- [JFreeChart](https://www.jfree.org/jfreechart/) for chart creation