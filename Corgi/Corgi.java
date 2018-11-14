/*
  Megan Jordal
 */

import java.util.Scanner;

public class Corgi {

  public static void main(String[] args) {

    System.out.print("Enter name of Corgi program file: ");
    Scanner keys = new Scanner( System.in );
    String name = keys.nextLine();

    Lexer lex = new Lexer( name );
    Parser parser = new Parser( lex );

    // start with <statements>
    Node root = parser.parseProgram();

    // execute the parse tree
    root.execute();

    System.out.println("Would you like to view the parse tree?[y/n]: ");
    char c = keys.next().charAt(0);
    if (c == 'y') {
      TreeViewer viewer = new TreeViewer("Parse Tree", 0, 0, 800, 500, root );
    }

  }// main

}
