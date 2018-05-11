package XACML2Bool

import scala.xml._
import Interpreter._
import XACML2Bool.Parser.XACMLParser

class Main {

  var policy: Elem = <PolicySet xmlns="urn:oasis:names:tc:xacml:3.0:core:schema:wd-17" PolicySetId="ChildrenPolicy"
                                PolicyCombiningAlgId="urn:oasis:names:tc:xacml:3.0:policy-combining-algorithm:deny-overrides" Version="1.0">
    <Target>
      <AnyOf>
        <AllOf>
          <Match MatchId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
            <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">children</AttributeValue>
            <AttributeDesignator
            MustBePresent="true"
            Category="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject"
            AttributeId="urn:oasis:names:tc:xacml:1.0:subject:subject-id"
            DataType="http://www.w3.org/2001/XMLSchema#string"/>
          </Match>
        </AllOf>
      </AnyOf>
    </Target>

    <Policy xmlns="urn:oasis:names:tc:xacml:3.0:core:schema:wd-17" PolicyId="ChildrenGeneralPolicy"
            RuleCombiningAlgId="urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:permit-unless-deny" Version="1.0">
      <Target>
        <AnyOf>
          <AllOf>
            <Match MatchId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
              <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">children</AttributeValue>
              <AttributeDesignator
              MustBePresent="true"
              Category="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject"
              AttributeId="urn:oasis:names:tc:xacml:1.0:subject:subject-id"
              DataType="http://www.w3.org/2001/XMLSchema#string"/>
            </Match>
          </AllOf>
        </AnyOf>
      </Target>
      <Rule RuleId="permit-rule" Effect="Permit">

      </Rule>
    </Policy>
    <Policy xmlns="urn:oasis:names:tc:xacml:3.0:core:schema:wd-17" PolicyId="ChildrenGasStovePolicy"
            RuleCombiningAlgId="urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:deny-overrides" Version="1.0">
      <Target>
        <AnyOf>
          <AllOf>
            <Match MatchId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
              <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">children</AttributeValue>
              <AttributeDesignator
              MustBePresent="true"
              Category="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject"
              AttributeId="urn:oasis:names:tc:xacml:1.0:subject:subject-id"
              DataType="http://www.w3.org/2001/XMLSchema#string"/>
            </Match>
            <Match MatchId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
              <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">gas_stove</AttributeValue>
              <AttributeDesignator
              MustBePresent="true"
              Category="urn:oasis:names:tc:xacml:3.0:attribute-category:resource"
              AttributeId="urn:oasis:names:tc:xacml:1.0:resource:resource-id"
              DataType="http://www.w3.org/2001/XMLSchema#string"/>
            </Match>
          </AllOf>
        </AnyOf>
      </Target>
      <Rule RuleId="deny-rule" Effect="Deny">

      </Rule>
    </Policy>
    <Policy xmlns="urn:oasis:names:tc:xacml:3.0:core:schema:wd-17" PolicyId="ChildrenFridgePolicy"
            RuleCombiningAlgId="urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:deny-overrides" Version="1.0">
      <Target>
        <AnyOf>
          <AllOf>
            <Match MatchId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
              <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">children</AttributeValue>
              <AttributeDesignator
              MustBePresent="true"
              Category="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject"
              AttributeId="urn:oasis:names:tc:xacml:1.0:subject:subject-id"
              DataType="http://www.w3.org/2001/XMLSchema#string"/>
            </Match>
            <Match MatchId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
              <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">fridge</AttributeValue>
              <AttributeDesignator
              MustBePresent="true"
              Category="urn:oasis:names:tc:xacml:3.0:attribute-category:resource"
              AttributeId="urn:oasis:names:tc:xacml:1.0:resource:resource-id"
              DataType="http://www.w3.org/2001/XMLSchema#string"/>
            </Match>
          </AllOf>
        </AnyOf>
      </Target>
      <Rule RuleId="deny-rule" Effect="Deny">
        <Target>
          <AnyOf>
            <AllOf>
              <Match MatchId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">Take off</AttributeValue>
                <AttributeDesignator
                MustBePresent="true"
                Category="urn:oasis:names:tc:xacml:3.0:attribute-category:action"
                AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id"
                DataType="http://www.w3.org/2001/XMLSchema#string"/>
              </Match>
            </AllOf>
          </AnyOf>
        </Target>
        <Condition>
          <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
            <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">food</AttributeValue>
            <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
              <AttributeDesignator AttributeId="food" Category="fridge"
                                   DataType="http://www.w3.org/2001/XMLSchema#string" MustBePresent="true"/>
            </Apply>
          </Apply>
        </Condition>
      </Rule>
    </Policy>
    <Policy xmlns="urn:oasis:names:tc:xacml:3.0:core:schema:wd-17" PolicyId="ChildrenCupboardPolicy"
            RuleCombiningAlgId="urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:deny-overrides" Version="1.0">
      <Target>
        <AnyOf>
          <AllOf>
            <Match MatchId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
              <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">children</AttributeValue>
              <AttributeDesignator
              MustBePresent="true"
              Category="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject"
              AttributeId="urn:oasis:names:tc:xacml:1.0:subject:subject-id"
              DataType="http://www.w3.org/2001/XMLSchema#string"/>
            </Match>
            <Match MatchId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
              <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">cupboard</AttributeValue>
              <AttributeDesignator
              MustBePresent="true"
              Category="urn:oasis:names:tc:xacml:3.0:attribute-category:resource"
              AttributeId="urn:oasis:names:tc:xacml:1.0:resource:resource-id"
              DataType="http://www.w3.org/2001/XMLSchema#string"/>
            </Match>
          </AllOf>
        </AnyOf>
      </Target>
      <Rule RuleId="deny-rule" Effect="Deny">

      </Rule>
    </Policy>
    <Policy xmlns="urn:oasis:names:tc:xacml:3.0:core:schema:wd-17" PolicyId="ChildrenCookerPolicy"
            RuleCombiningAlgId="urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:deny-unless-permit" Version="1.0">
      <Target>
        <AnyOf>
          <AllOf>
            <Match MatchId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
              <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">children</AttributeValue>
              <AttributeDesignator
              MustBePresent="true"
              Category="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject"
              AttributeId="urn:oasis:names:tc:xacml:1.0:subject:subject-id"
              DataType="http://www.w3.org/2001/XMLSchema#string"/>
            </Match>
            <Match MatchId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
              <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">cooker</AttributeValue>
              <AttributeDesignator
              MustBePresent="true"
              Category="urn:oasis:names:tc:xacml:3.0:attribute-category:resource"
              AttributeId="urn:oasis:names:tc:xacml:1.0:resource:resource-id"
              DataType="http://www.w3.org/2001/XMLSchema#string"/>
            </Match>
          </AllOf>
        </AnyOf>
      </Target>
      <Rule RuleId="permit-rule" Effect="Permit">
        <Condition>
          <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
            <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">parents</AttributeValue>
            <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
              <AttributeDesignator AttributeId="assist" Category="cook"
                                   DataType="http://www.w3.org/2001/XMLSchema#string" MustBePresent="true"/>
            </Apply>
          </Apply>
        </Condition>
      </Rule>
    </Policy>

  </PolicySet>


  def printX(): Unit = {
//
//    var p = policy.toString()
//    var q = scala.xml.XML.loadString(p);
//    // q = scala.xml.XML.load(url:URL) // URL로 파일에 접근하는 것도 가능한 듯
//
//    // \는 직계 자손중에서 찾고
//    // \\는 직계 아닌것도 다 찾음, 한개면 Elem, 여러개면 NodeSeq
//    // https://medium.com/@harittweets/working-with-xml-in-scala-bd6271a1e178

    //Parser Test
    val syntaxTree = XACMLParser.parseAll(policy)
    println("SyntaxTree: " + syntaxTree)

    //Interpreter Test

    def interpretVia(mode:Mode) =
      if(mode is "Permit") PermitInterpreter
//      else if(mode is "Deny") DenyInterpreter
      else throw new Exception("Permit/Deny Only")

    val onPermitMode:Interpreter = interpretVia(Permit)
//    val onDenyMode:Interpreter = interpretVia(Deny)

    val permitSat = onPermitMode interpretAll syntaxTree
//    val denySat = onDenyMode interpretAll syntaxTree

    println("OnPermit SAT: " + permitSat)
//    println("OnDeny SAT: " + denySat)

//
//    //CNFConverter Test
//    val cnf = CNFConverter.convertSAT2CNF(sat)
//    println("CNF: " + cnf)

  }

  def process(xacml:String): Unit = {

    def readPolicy(filename:String):Elem = this policy

    def writeFile(filename:String, content:String) = 1

    val policy = readPolicy(xacml)

    val syntaxTree = XACMLParser.parseAll(policy)
//    val sat = Interpreter.interpretAll(syntaxTree)
//    val cnf = CNFConverter.convertSAT2CNF(sat)

//    writeFile(xacml+".cnf", cnf)

  }

}