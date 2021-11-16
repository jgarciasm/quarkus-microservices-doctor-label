package com.cocus.doctor.label.utils;

import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

//Before executing this remove the first line of conditions.sql
public class ConditionsToSQL {

	public static void main(String[] args) {

	    String file = "/home/jonad/cocus/First Interviews/quarkus-doctor-label/conditions.csv";
	    String fileOutput = "/home/jonad/cocus/First Interviews/quarkus-doctor-label/importNew.sql";

	        try (Stream<String> input = Files.lines(Paths.get(file));
	        		PrintWriter output = new PrintWriter(fileOutput, "UTF-8")) {
	        	
	        	input.forEach(e -> {
	        		output.println("INSERT INTO label(id, code, description)");
	        		int firstSpacePos = 0;
	        		for (int index = 0; index < e.length(); index++) {
	        			   if (Character.isWhitespace(e.charAt(index))) {
	        				   firstSpacePos = index;
	        				   break;
	        			   }
	        			}
	        		String code = e.substring(0, firstSpacePos).replace("\"", "").strip();
	        		String desc = e.substring(firstSpacePos).replace("\"", "").trim();
	        		while (desc.endsWith(",")) {
						desc = desc.substring(0, desc.length()-2).trim();
					}
	        		String query = String.format("VALUES (nextval(\'hibernate_sequence\'), \'%s\', \'%s\');", code, desc);
	        		output.println(query);
	        		});
	        } catch (Exception e2) {
	          e2.printStackTrace();
	        }

	}

}
