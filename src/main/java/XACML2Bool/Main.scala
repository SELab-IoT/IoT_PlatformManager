package XACML2Bool

import scala.xml._

class Main {

  var policy = <PolicySet           PolicySetId="urn:oasis:names:tc:xacml:3.0:example:policysetid:1"
                                    Version="1.0"
                                    PolicyCombiningAlgId="urn:oasis:names:tc:xacml:3.0:policy-combining-algorithm:deny-overrides">
    <Description> Example policy set. </Description>
    <Target>
      <AnyOf>
        <AllOf>
          <Match MatchId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
            <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">urn:example:med:schema:records</AttributeValue>
            <AttributeDesignator MustBePresent="false"
                                 Category="urn:oasis:names:tc:xacml:3.0:attribute-category:resource"
                                 AttributeId="urn:oasis:names:tc:xacml:2.0:resource:target-namespace"
                                 DataType="http://www.w3.org/2001/XMLSchema#string"/>
          </Match>
        </AllOf>
      </AnyOf>
    </Target>
    <PolicyIdReference> urn:oasis:names:tc:xacml:3.0:example:policyid:3 </PolicyIdReference>
    <Policy PolicyId="urn:oasis:names:tc:xacml:3.0:example:policyid:2"
            RuleCombiningAlgId="urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:permit-unless-deny"
            Version="1.0">
      <Target/>
      <Rule RuleId="urn:oasis:names:tc:xacml:3.0:example:ruleid:1" Effect="Permit"/>
      <Rule RuleId="urn:oasis:names:tc:xacml:3.0:example:ruleid:2" Effect="Permit"/>
      <Rule RuleId="urn:oasis:names:tc:xacml:3.0:example:ruleid:4" Effect="Deny"/>
    </Policy>

    <Policy PolicyId="urn:oasis:names:tc:xacml:3.0:example:policyid:2"
            RuleCombiningAlgId="urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:permit-unless-deny"
            Version="1.0">
      <Target/>
      <Rule RuleId="urn:oasis:names:tc:xacml:3.0:example:ruleid:1" Effect="Permit"/>
      <Rule RuleId="urn:oasis:names:tc:xacml:3.0:example:ruleid:2" Effect="Permit"/>
      <Rule RuleId="urn:oasis:names:tc:xacml:3.0:example:ruleid:4" Effect="Deny"/>
    </Policy>

    <Policy PolicyId="urn:oasis:names:tc:xacml:3.0:example:policyid:2"
            RuleCombiningAlgId="urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:permit-unless-deny"
            Version="1.0">
      <Target/>
      <Rule RuleId="urn:oasis:names:tc:xacml:3.0:example:ruleid:1" Effect="Permit"/>
      <Rule RuleId="urn:oasis:names:tc:xacml:3.0:example:ruleid:2" Effect="Permit"/>
      <Rule RuleId="urn:oasis:names:tc:xacml:3.0:example:ruleid:4" Effect="Deny"/>
    </Policy>
  </PolicySet>


  def printX = {
//
//    var p = policy.toString()
//    var q = scala.xml.XML.loadString(p);
//    // q = scala.xml.XML.load(url:URL) // URL로 파일에 접근하는 것도 가능한 듯
//
//    // \는 직계 자손중에서 찾고
//    // \\는 직계 아닌것도 다 찾음, 한개면 Elem, 여러개면 NodeSeq
//    // https://medium.com/@harittweets/working-with-xml-in-scala-bd6271a1e178

    //Parser Test
    val syntaxTree = Parser.parseAll(policy)
    println("SyntaxTree: " + syntaxTree)

    //Interpreter Test
    val sat = Interpreter.interpretAll(syntaxTree)
    println("SAT: " + sat)

    //CNFConverter Test
    val cnf = CNFConverter.convertSAT2CNF(sat)
    println("CNF: " + cnf)

  }

  def process(xacml:String)= {

    def readPolicy(filename:String):Elem = ???

    def writeFile(filename:String, content:String) = ???

    val policy = readPolicy(xacml)

    val syntaxTree = Parser.parseAll(policy)
    val sat = Interpreter.interpretAll(syntaxTree)
    val cnf = CNFConverter.convertSAT2CNF(sat)

    writeFile(xacml+".cnf", cnf)

  }

}