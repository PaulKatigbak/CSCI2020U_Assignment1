# CSCI2020U_Assignment1

This assignment tasks us to implement a Spam Detector/Filter. The following implementation is done by using the source materials and various references.

##Project Information:

This project implents a Bayesiam Interface based on Baye's Theorem. The user will provide a directory path containing the train and test folders and the program will read and analyze all the files in accordance with the spam and ham algorithm.

##Improvements:
####Graphical User Interface:
Visual improvements are done to the basic application window. Some background colors are added to bring some personality to the application.

Interface has been improved by adding buttons to specify whether the user want to "train" or "test" the chosen directory.

##How-to Run:
1. Copy the repository link and clone the repo to your local machine.
2. Open IntelliJ IDE, Eclipse, or any ide that supports JavaFX.
3. Open the repo project.
4. If the program cannot find JavaFX library, add the JavaFX library from your local machine to the program.
5. If using IntelliJ, simply press Ctrl+Alt+Shift+S to open Project Structure, and add your local JavaFX library to the project. Note: If using any other ide, research on how to add the library.
6. Add the required VM arguments.
7. Start the application.
8. Choose the "train" button, and select the directory containing the train files.
9. After training, choose the "test" button, and select the directory containing the test files.
10. The application will then show the accuracy, precision, and all other data to the user.

####Note:

The application is hard-coded to search for a specific directory structure and name.
 "train" directory must have its subdirectories named "ham","ham2", and "spam". "test" directory must have its subdirectories named "ham" and "spam".

##References:

StackOverflow

Lecture Modules and Examples

https://en.wikipedia.org/wiki/Naive_Bayes_spam_filtering

https://towardsdatascience.com/na%C3%AFve-bayes-spam-filter-from-scratch-12970ad3dae7