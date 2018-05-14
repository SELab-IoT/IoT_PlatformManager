package XACML2Bool.Interpreter

object Builder {
  def con(terms:String*):String =
    if(terms contains FALSE) FALSE
    else if(terms forall TRUE.equals) TRUE
    else buildWith(AND, terms filterNot TRUE.equals)

  def dis(terms:String*):String =
    if(terms contains TRUE) TRUE
    else if(terms forall FALSE.equals) FALSE
    else buildWith(OR, terms filterNot FALSE.equals)

  def neg(term:String):String =
    if(term equals TRUE) FALSE
    else if(term equals FALSE) TRUE
    else if(term startsWith NOT) term replaceFirst (NOT, "")
    else NOT + term

//  def con(terms:String*):String = buildWith(AND, terms)
//  def dis(terms:String*):String = buildWith(OR, terms)
//  def neg(term:String):String = NOT + wrap(term)

  def buildWith(op:String, terms:Seq[String]):String =
    if(terms.length == 0) TRUE
    else if(terms.length == 1) terms.head
    else op + joinAll(terms:_*)


  def wrap(n:String):String = "(" + n + ")"
  // Constraint. n.length must be >= 2
  def joinAll(n: String*):String =
    wrap(n reduce (_+" "+_))

  def AND = "*"
  def OR = "+"
  def NOT = "-"

  def TRUE = "$TRUE$"
  def FALSE = "$FALSE$"
}
