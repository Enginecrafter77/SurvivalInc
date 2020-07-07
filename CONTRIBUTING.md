# Contributing

In this document, you can find the contributing guidelines that you should follow
if you want to contribute into the project.

Contributions come in several categories. You can choose one, and follow the guidelines
described under the specific category.

## Issues
This category is pretty straightforward. If you found an issue,
or you want to enhance the project in some way, you open up a
new issue on github page. Most people should use the provided
issue templates. If you are 100% sure none of the templates apply
to your problem, create an issue without template, but this is 
generally discouraged, since it's easy to fall for low-effort issues,
and low-effort issues will be dismissed. Issues should generally contain
detailed info about your problem. For example, bug reports should contain
steps to reproduce, as well as your forge version and mod list. Crash reports
do not need to contain these information, since these are already present in
the crash report the game generates itself.

## Wiki
If you want to contribute to the wiki, such as writing additional information,
or fixing typos, the best way is to follow this procedure:
 * Clone the wiki (https://github.com/Enginecrafter77/SurvivalInc.wiki.git).
 * Make your changes
 * Commit your changes
     * Preferably, use your github account as the author and commiter.
 * Archive the *ENTIRE* directory. You can use ZIP or TAR (with gz or xz). Do **NOT** use RAR format!
     * Make sure you included the `.git` directory, or your changes won't be attributed to you!
     * Malicious ZIP files will result in permanent ban. It doesn't matter how much you meant it as a joke.
 * Send the file in a new issue using the *Wiki Edit* template.
 * Wait for review

## Code
This is by far the most controversial part. Users who decide to contribute
some code should strictly follow the mentioned guidelines.
 * The code should use the same formatting as most of the codebase
    * Use TABS instead of SPACES. If you IDE expands TABS to SPACES, disable that feature.
    * Use standard indentation. Weird indentation patterns will not be accepted.
    * Function and nested class curly brackets should be in new line. Public class (in separate file) curly brackets sould be in same line.
    * For more details, see the existing code.
 * Document your code. Documented code receives far better rating than undocumented code.
    * Use normal comments (/\*...\*/ or //...) only for inner code comments.
    * Javadoc-capable elements, such as classes, methods, variables etc. should use Javadoc comment blocks (/\*\*...\*/) 
 * The code should be clean, and easy to understand
    * In case your code is not easily understandable, make sure you document what it does, how it works, and why it works. Obscure undocumented code will **NOT** be tolerated!
 * Aim for elegant, extensible and performant solutions.
    * This includes using preferable algorithms and data structures. 

## Art
If you would like to contribute some artwork, open up a new issue labeled with "enhancement" and describe
the changes you would like to make.

Some examples of artwork contributions include:
 * Textures
 * Sounds
 * OpenGL Shaders (currently only one, and yes, they are considered more artwork than code)

## Translations
Unfortunately, since the project is still in early phases of development, the translations would easily
become obsolete. However, if you feel like you would like to contribute a translation, follow this procedure.

 * Fork the repository
 * Clone your fork of the repository
 * Add your translation(s)
 * Commit your changes
 * Push them to your forked repository
 * Create a Pull Request to the main repository
 * Wait for review
