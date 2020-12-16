package com.company;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        try {
            MyGrammar myGrammar = new MyGrammar("C:\\Users\\Dan_B\\Documents\\FLC\\lab6\\src\\com\\company\\grammar.in");

            while (true) {
                System.out.println("1. See terminals\n2. See onnterminals\n3. See all productions\n4. See productions for a nonterminal\n5.See start symbol\n6. First Function");
                Scanner in = new Scanner(System.in);
                String option = in.nextLine();
                if (option.equals("1")) {
                    System.out.println(myGrammar.getTerminals());
                }
                if (option.equals("2")) {
                    System.out.println(myGrammar.getNonterminals());
                }
                if (option.equals("3")) {
                    System.out.println(myGrammar.getProductions());
                }

                if (option.equals("4")) {
                    String nonterminal = in.nextLine();
                    try {
                        System.out.println(myGrammar.getProductionsForNonterminal(nonterminal));
                    }catch (Exception e){
                        System.out.println(e);
                    }
                }
                if (option.equals("5")) {
                    System.out.println(myGrammar.getFirst());
                }
                if (option.equals("6")) {
                    String nonterminal = in.nextLine();
                    System.out.println(myGrammar.getFirstB(nonterminal));
                }
                if (option.equals("7")) {
                    String nonterminal = in.nextLine();
                    System.out.println(myGrammar.getFollow(nonterminal));
                }
                if (option.equals("8")){
                    System.out.println(myGrammar.getParsingTable());
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
