# CppSyntaxPane

**`CppSyntaxPane`** is a stripped down version of [JSyntaxPane](https://github.com/nordfalk/jsyntaxpane) that only supports the syntax of the C++ programming language. I created **`CppSyntaxPane`** for use in my [ATTiiny10IDE](https://github.com/wholder/ATTiny10IDE) project and, to save space, I've tried to remove as much code unrelated to C++ as possible.  In addition, the dynamic configuration feature based on `Properties` files has been removed and replaced with statically-assigned variables.  This reduces the total size of **`CppSyntaxPane`** to less than 68 KB (vs 420 KB for JSyntaxPane.)

#### Features Inherited from JSyntaxPane

 + Cut, Copy, Paste and Select All
 + Undo and Redo
 + Indent and Unindent
 + Find and Find Next (with support for regex expression, ignore case, etc.)
 + Replace and Replace All
 + Search and Highlight
 + Goto Line Number

You can run the class `TestEditor` to see **`CppSyntaxPane`** in action and an executable .jar file for **`CppSyntaxPane`** can be [downloaded here](https://github.com/wholder/CppSyntaxPane/tree/master/out/artifacts/CppSyntaxPane_jar).

<p align="center"><img src="https://github.com/wholder/CppSyntaxPane/blob/master/images/CppSyntaxPane%20Screenshot.png"></p>

The original project JSyntaxPane can be found [on google-code](http://code.google.com/p/jsyntaxpane/). The version of JSyntaxcPane this code is based on is a fork from the 0.9.6 branch with [Hanns Holger Rutz](https://github.com/Sciss/SyntaxPane)'s work applied.

The original project is (C)opyright by Ayman Al-Sairafi and released under the [Apache License, Version 2.0](http://github.com/Sciss/JSyntaxPane/blob/master/licenses/JSyntaxPane-License.txt).


