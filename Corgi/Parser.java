/*
    This class provides a recursive descent parser 
    for Corgi (a simple calculator language),
    creating a parse tree which can be interpreted
    to simulate execution of a Corgi program
*/

public class Parser {

    private Lexer lex;

    public Parser( Lexer lexer ) {
        lex = lexer;
    }

    public Node parseProgram() {
        System.out.println("------> parsing <program>:");

        Node first = parseFuncCall();
        Token token = lex.getNextToken();

        if (token.isKind("eof")) {
            return new Node("prgrm", first, null, null);
        } else {
            lex.putBackToken(token);
            Node second = parseFuncDefs();
            return new Node("prgrm", first, second, null);
        }
    }

    private Node parseFuncDefs() {
        System.out.println("-----> parsing <funcDefs>");

        Node first = parseFuncDef();

        // look ahead to see if there are more function's
        Token token = lex.getNextToken();

        if ( token.isKind("eof") ) {
            return new Node( "fDefs", first, null, null );
        }
        else {
            lex.putBackToken( token );
            Node second = parseFuncDefs();
            return new Node( "fDef", first, second, null );
        }
    }

    private Node parseParams() {
        System.out.println("-----> parsing <params>");
        Token token = lex.getNextToken();

        if(token.matches("single", ",")) {
            Node first = parseParams();
            return new Node(token.getDetails(),first,null,null);
        }

        else {
            lex.putBackToken(token);
            return new Node("var",token.getDetails(),null,null,null);
        }
    }


    private Node parseStatements() {
        System.out.println("-----> parsing <statements>:");

        Node first = parseStatement();

        // look ahead to see if there are more statement's
        Token token = lex.getNextToken();

        if ( token.isKind("eof") ) {
            return new Node( "stmts", first, null, null );
        }
        else {
            lex.putBackToken( token );
            Node second = parseStatements();
            return new Node( "stmts", first, second, null );
        }
    }// <statements>

    private Node parseFuncCall() {
        System.out.println("-------> parsing <funcCall>:");
        Token token = lex.getNextToken();
        String function = token.getDetails();
        token = lex.getNextToken();
        errorCheck(token,"single", "(");
        token = lex.getNextToken();

        if (token.isKind("single")) {
            return new Node("fcall", function, null, null, null);
        }
        else {
            lex.putBackToken(token);
            Node first = parseArgs();
            token = lex.getNextToken();
            errorCheck(token, "single", ")");
            return new Node("fcall", function, first, null, null);
        }
/*
        if (token.isKind("var")) {
            token = lex.getNextToken();
            errorCheck(token, "single", "(");
            token = lex.getNextToken();
            if (token.isKind("args")) {
                Node first = parseArgs();
                token = lex.getNextToken();
                errorCheck(token, "single", ")");

                return new Node(token.getDetails(), first, null, null);
            }
            errorCheck(token, "single", ")");

            return new Node(token.getDetails(), null, null, null);
        }
        System.err.println("This is an error in parseFuncCall");
        return null; //This is an error
        */
    }

    private Node parseArgs() {
        System.out.println("-----> parsing <args>:");
        Node first = parseExpr();
        Token token = lex.getNextToken();

        if(token.matches("single", ",")) {
            Node second = parseArgs();
            return new Node(token.getDetails(), first, second, null);
        }
        else {
            lex.putBackToken(token);
            return new Node(token.getDetails(), first, null, null);
        }
    }
//TODO
    private Node parseStatement() {
        System.out.println("-----> parsing <statement>:");

        Token token = lex.getNextToken();

        // ---------------->>> string
        if (token.isKind("string")) {
            return new Node("print", token.getDetails(),null,null,null);
        }

        // --------------->>>   <var> = <expr>
        else if ( token.isKind("var") ) {
            String varName = token.getDetails();
            token = lex.getNextToken();
            errorCheck(token, "single", "=");
            Node first = parseExpr();
            return new Node("sto", varName, first, null, null);
        }

        //---------------->>> <funcCall>
        else if(token.isKind("funcCall")) {
            return new Node("funcCall", token.getDetails(), null, null, null);
        }

        else if ( token.matches("bif0","nl") ) {
            token = lex.getNextToken();
            errorCheck( token, "single", "(" );
            token = lex.getNextToken();
            errorCheck( token, "single", ")" );
            return new Node( "nl", null, null, null );
        }

        else if ( token.matches("bif1","print") ) {
            token = lex.getNextToken();
            errorCheck( token, "single", "(" );
            Node first = parseExpr();
            token = lex.getNextToken();
            errorCheck( token, "single", ")" );
            return new Node( "print", first, null, null );
        }

        if( token.isKind("bif1")|| token.isKind("bif2")) {
            Node first = parseParams();
            token = lex.getNextToken();
            errorCheck( token, "single", "(" );
            return new Node ( token.getDetails(), first, null, null);
        }

        else if ( token.matches("var","return") ){
            Node first = parseExpr();
            return new Node("return", first, null, null);
        }


        // ---------------->>>  print <string>  or   print <expr>
        /*
        else if ( token.isKind("print") ) {
            token = lex.getNextToken();

            if ( token.isKind("string") ) {// print <string>
                return new Node( "prtstr", token.getDetails(), null, null, null );
            }
            else {// must be first token in <expr>
                // put back the token we looked ahead at
                lex.putBackToken( token );
                Node first = parseExpr();
                return new Node( "prtexp", first, null, null );
            }
            // ---------------->>>  newline
        }

        else if ( token.isKind("newline") ) {
            return new Node( "nl", null, null, null );
        } */


        else {
            System.out.println("Token " + token +
                    " can't begin a statement");
            System.exit(1);
            return null;
        }

    }// <statement>

    private Node parseExpr() {
        System.out.println("-----> parsing <expr>");

        Node first = parseTerm();

        // look ahead to see if there's an addop
        Token token = lex.getNextToken();

        if ( token.matches("single", "+") ||
                token.matches("single", "-")
        ) {
            Node second = parseExpr();
            return new Node( token.getDetails(), first, second, null );
        }
        else {// is just one term
            lex.putBackToken( token );
            return first;
        }

    }// <expr>

    private Node parseTerm() {
        System.out.println("-----> parsing <term>");

        Node first = parseFactor();

        // look ahead to see if there's a multop
        Token token = lex.getNextToken();

        if ( token.matches("single", "*") ||
                token.matches("single", "/")
        ) {
            Node second = parseTerm();
            return new Node( token.getDetails(), first, second, null );
        }
        else {// is just one factor
            lex.putBackToken( token );
            return first;
        }

    }// <term>

    private Node parseFactor() {
        System.out.println("-----> parsing <factor>");

        Token token = lex.getNextToken();

        if ( token.isKind("num") ) {
            return new Node("num", token.getDetails(), null, null, null );
        }

        else if ( token.isKind("var") ) {
            return new Node("var", token.getDetails(), null, null, null );
        }

        else if ( token.matches("single","(") ) {
            Node first = parseExpr();
            token = lex.getNextToken();
            errorCheck( token, "single", ")" );
            return first;
        }

        else if ( token.matches("single","-") ) {
            Node first = parseFactor();
            return new Node("opp", first, null, null );
        }

        else if (token.isKind("funcCall")) {
            return new Node("funcCall", token.getDetails(), null, null, null);
        }

        else {
            System.out.println("Can't have factor starting with " + token );
            System.exit(1);
            return null;
        }

    }// <factor>


//<funcDefs> -> <funcDef> | <funcDef> <funcDefs>

    private Node parseFuncDef() {
        System.out.println("-----> parsing <funcDef>:");

        Token token = lex.getNextToken();
        errorCheck( token, "var", "def" );
        token = lex.getNextToken();
        String funcName = token.getDetails();
        token = lex.getNextToken();
        errorCheck( token, "single", "(" );
        token = lex.getNextToken();

        //<params> not part of it
        if(token.matches("single",")")){
            token = lex.getNextToken();

            //<stmts> not part it
            if(token.getDetails().equals("end")) {
                return new Node("funcDef", funcName, null, null, null);
            }
            //<stmts> is part of it
            else{
                lex.putBackToken(token);
                Node second = parseStatements();
                return new Node("funcDef", funcName, null, second, null);
            }
        }
        //<params> is part of it
        else{
            lex.putBackToken(token);
            Node first = parseParams();
            token = lex.getNextToken();

            //<stmts> not part of it
            if(token.getDetails().equals("end")) {
                return new Node("funcDef", funcName, first, null, null);
            }
            //<stmts> is part of it
            else{
                lex.putBackToken(token);
                Node second = parseStatements();
                return new Node("funcDef", funcName, first, second, null);
            }
        }
    }
    // check whether token is correct kind and details
    private void errorCheck( Token token, String kind, String details ) {
        if( ! token.isKind( kind ) ||
                ! token.getDetails().equals( details ) ) {
            System.out.println("Error:  expected " + token +
                    " to be kind=" + kind +
                    " and details=" + details );
            System.exit(1);
        }
    }
}
