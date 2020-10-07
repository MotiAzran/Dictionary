package com.moti;

import java.io.File;
import java.io.FileWriter;
import java.util.Map;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        try {
            Dictionary dic = new Dictionary();

            dic.add_term("Moti", "Azran");
            dic.add_term("Shoval", "Weitzman");
            dic.add_term("Amit", "Azran");
            dic.add_term("Orit", "Abisdris");

            print_dictionary(dic);
            
            try {
                dic.delete_term("Eli");
            } catch (TermNotExistsException e) {
                System.out.println("Got " + e.getMessage());
            }

            try {
                dic.add_term("Moti", "Azran");
            } catch (TermExistsException e) {
                System.out.println("Got " + e.getMessage());
            }

            FileWriter dic_file = new FileWriter("dic.txt");
            dic.write_to_file(dic_file);
            dic_file.close();

            Dictionary dic2 = Dictionary.create_dictionary_from_file(new Scanner(new File("dic.txt")));

            print_dictionary(dic2);

            dic2.update_term("Orit", "Azran");
            dic2.delete_term("Shoval");

            print_dictionary(dic2);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void print_dictionary(Dictionary dic) {
        for (Map.Entry<String, String> e : dic) {
            System.out.printf("%s - %s\n", e.getKey(), e.getValue());
        }
        System.out.println();
    }
}
