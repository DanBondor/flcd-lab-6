package com.company;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.*;

class MyGrammar {
    private List<String> terminals;
    private List<String> nonterminals;
    private HashMap<String, List<List<String>>> productions;
    private String first;
    //private List<List<String>> parsingTable;
    private Map<String, Map<String, List<String>>> parseTable;
    private Stack<String> alpha;
    private Stack<String> beta;
    private Stack<String> pi;

    MyGrammar(String filename) throws Exception {
        this.terminals = new ArrayList<>();
        this.nonterminals = new ArrayList<>();
        this.productions = new HashMap<>();
        //this.parsingTable = new ArrayList<>();
        this.parseTable = new HashMap<>();
        this.alpha = new Stack<>();
        this.beta = new Stack<>();
        this.pi = new Stack<>();
        this.readGrammar(filename);
    }

    List<String> getTerminals() {
        return terminals;
    }

    List<String> getNonterminals() {
        return nonterminals;
    }

    HashMap<String, List<List<String>>> getProductions() {
        return productions;
    }

    String getFirst() {
        return first;
    }

    private void readGrammar(String fileName) throws Exception {
        File myObj = new File(fileName);
        Scanner myReader;
        try {
            myReader = new Scanner(myObj);
        } catch (FileNotFoundException e) {
            System.out.println("No such file");
            return;
        }
        String[] stateLine = myReader.nextLine().split(" ");
        Collections.addAll(this.nonterminals, stateLine);

        String[] alphaLine = myReader.nextLine().split(" ");
        Collections.addAll(this.terminals, alphaLine);

        this.first = myReader.nextLine();

        if (!this.nonterminals.contains(this.first)) {
            throw new Exception("Start symbol must be a non-terminal!");
        }

        while (myReader.hasNextLine()) {
            String[] transSet = myReader.nextLine().split(",");
            String nonterminal = transSet[0];
            if (!this.nonterminals.contains(nonterminal)) {
                throw new Exception(nonterminal + " is not a non-terminal!");
            }
            List<List<String>> transSetToAdd = new ArrayList<>();
            for (int i = 1; i < transSet.length; i++) {
                ArrayList<String> oneTransition = new ArrayList<>();
                Collections.addAll(oneTransition, transSet[i].split(" "));
                transSetToAdd.add(oneTransition);
            }
            checkProductionsSet(transSetToAdd);
            this.productions.put(nonterminal, transSetToAdd);
        }
        myReader.close();
    }

    private void checkProductionsSet(List<List<String>> transSetToAdd) throws Exception {
        for (List<String> e : transSetToAdd) {
            for (String character : e) {
                if (!terminals.contains(character) && !nonterminals.contains(character) && !character.equals("eps")) {
                    throw new Exception(character + " is not a terminal!");
                }
            }
        }
    }

    List<List<String>> getProductionsForNonterminal(String nonTerminal) throws Exception {
        if (this.nonterminals.contains(nonTerminal))
            return this.productions.get(nonTerminal);
        throw new Exception(nonTerminal + " getProds: is not a nonterminal");
    }


    public List<String> getFirst(String nonterminal) throws Exception {
        ArrayList<String> first = new ArrayList<>();
        for (List<String> prod : getProductionsForNonterminal(nonterminal)) {
            if (!first.contains(prod.get(0)) && !prod.get(0).equals(nonterminal)) {
                first.add(prod.get(0));
            }

        }

        for (int i = 0; i < first.size(); i++) {
            if (this.nonterminals.contains(first.get(i))) {
                List<String> newVars = new ArrayList<>();
                for (List<String> prod : getProductionsForNonterminal(first.get(i))) {
                    newVars.add(prod.get(0));
                }
                first.remove(i);

                for (String el : newVars) {
                    if (!first.contains(el) && !el.equals(nonterminal)) {
                        first.add(el);
                    }
                }
            }
        }
        return first;
    }

    public Set<String> getFirstB(String nonterminal) throws Exception {
        if (!nonterminals.contains(nonterminal)) {
            Set<String> arr = new HashSet<>();
            arr.add(nonterminal);
            return arr;
        }


        Set<String> arr = new HashSet<>();
        for (List<String> prod : getProductionsForNonterminal(nonterminal)) {
            if (!prod.get(0).equals(nonterminal)) {
                arr.addAll(this.getFirstB(prod.get(0)));
            }
        }
        return arr;
    }

    Set<String> getFollow(String nonterminal) throws Exception {
        Set<String> follow = new HashSet<>();
        if (nonterminal.equals(this.getFirst())) {
            follow.add("$");
            return follow;
        }
        for (String fromSymbol : this.getProductions().keySet()) {
            for (List<String> prod : this.getProductions().get(fromSymbol)) {
                for (int i = 0; i < prod.size(); i++) {
                    if (prod.get(i).equals(nonterminal)) {
                        if (i != prod.size() - 1) {
                            if (this.nonterminals.contains(prod.get(i + 1))) {
                                follow.addAll(this.getFirstB(prod.get(i + 1)));
                            } else {
                                follow.add(prod.get(i + 1));
                            }
                        }
                    }
                }
            }
        }
        return follow;
    }

    public Map<String, Map<String, List<String>>> getParsingTable() throws Exception {

        for (String nonterminal :productions.keySet())
        {
            Map<String, List<String>> aux = new HashMap<>();

            for (List<String> production : productions.get(nonterminal))
            {
                if (!production.get(0).equals("eps"))
                {
                    for (String first : getFirstB(production.get(0)))
                    {
                        aux.put(first, production);
                    }
                }
                else
                {
                    for (String follow: getFollow(nonterminal))
                    {
                        aux.put(follow, production);
                    }
                }
            }
            parseTable.put(nonterminal, aux);
        }

        return parseTable;
    }

    /*public boolean parse(List<String> w)
    {

    }*/

    public boolean shouldGetFollow(String nonterminal) throws Exception {
        List<String> epsList = new ArrayList<>();
        epsList.add("eps");
        return getProductionsForNonterminal(nonterminal).contains(epsList);
    }

}