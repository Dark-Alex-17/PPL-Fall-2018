  // ask this node to execute itself
  // (for nodes that don't return a value)
   public void execute() {

      if ( kind.equals("stmts") ) {
          // insert code here for Exercise 15
      }

      else if ( kind.equals("prtstr") ) {
         System.out.print( info );
      }
      
      else if ( kind.equals("prtexp") ) {
          // insert code here for Exercise 15
      }
      
      else if ( kind.equals("nl") ) {
         System.out.print( "\n" );
      }
      
      else if ( kind.equals("sto") ) {
          // insert code here for Exercise 15
      }
      
      else {
         error("Unknown kind of node [" + kind + "]");     
      }

   }// execute
    
   // compute and return value produced by this node
   public double evaluate() {

      if ( kind.equals("num") ) {
         return Double.parseDouble( info );
      }

      else if ( kind.equals("var") ) {
          // insert code here for Exercise 15
      }

      else if ( kind.equals("+") || kind.equals("-") ) {
          // insert code here for Exercise 15
      }

      else if ( kind.equals("*") || kind.equals("/") ) {
          // insert code here for Exercise 15
       }
 
       else if ( kind.equals("input") ) {
          return keys.nextDouble();          
       }
       
       else if ( kind.equals("sqrt") || kind.equals("cos") ||
                 kind.equals("sin") || kind.equals("atan")    
               ) {
          double value = first.evaluate();

          if ( kind.equals("sqrt") )
             return Math.sqrt(value);
          else if ( kind.equals("cos") )
             return Math.cos( Math.toRadians( value ) );
          else if ( kind.equals("sin") )
             return Math.sin( Math.toRadians( value ) );
          else if ( kind.equals("atan") )
             return Math.toDegrees( Math.atan( value ) );
          else {
             error("unknown function name [" + kind + "]");
             return 0;
          }
            
       }
       
       else if ( kind.equals("pow") ) {
          // insert code here for Exercise 15
       }

       else if ( kind.equals("opp") ) {
          // insert code here for Exercise 15
       }

       else {
          error("Unknown node kind [" + kind + "]");
          return 0;
       }
       
   }// evaluate
