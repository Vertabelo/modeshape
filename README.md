ModeShape (Vertabelo fork)
=========================

This is a branch from the https://github.com/ModeShape/modeshape repository made for the Vertabelo.com project.

As part of the customization, support for new SQL language functions for individual databases was added.

How to compile the project
-------------------------

Compilation requires Java 11 or newer and Maven 3.2.3. To run exactly these versions of the software, it will be most convenient to use https://sdkman.io/ - how to install it is described on that page.

Configuring the appropriate versions:

    sdk install java 11.0.26-tem
    sdk use java 11.0.26-tem

    sdk install maven 3.2.3
    sdk use maven 3.2.3

Build:

    cd <directory_with_the_cloned_project>/
    mvn install
