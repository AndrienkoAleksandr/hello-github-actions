package org.eclipse.che;

import java.io.*;
import java.nio.file.Path;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class App 
{
    public static void main( String[] args ) throws IOException
    {
        String repoPath = System.getenv("GITHUB_WORKSPACE");
        if (repoPath == null) {
            throw new RuntimeException("Env variable \"GITHUB_WORKSPACE\" wasn't set.");
        }

        File goSum = new File(Path.of(repoPath, "go.sum").toUri());
        Collection<String> goSumDeps = readGoSumFile(goSum);
//        System.out.println( "Hello World!" + depList);

        String patternString = "\\|\\s+\\[(.*)@.*\\]\\(.*\\)\\s.*";
        Pattern pattern = Pattern.compile(patternString);
        File depListFile = new File(Path.of(repoPath,"DEPENDENCIES.md").toUri());
        List<String> definedDeps = readFile(depListFile);

        List<String> declaredDep = new ArrayList<>();
        for (String line: definedDeps) {
            Matcher matcher = pattern.matcher(line);
            boolean matches = matcher.matches();
            if (matches) {
                declaredDep.add(matcher.group(1));
            }
        }

        System.out.println("===================");
        for (String goSumDep: goSumDeps) {
            if (!declaredDep.contains(goSumDep)) {
                System.out.println("Add new dependency to DEPENDENCIES.md file" + goSumDep);
            }
        }
    }

    public static Collection<String> readGoSumFile(File file) throws IOException {
        List<String> lines = new ArrayList<>();
        InputStream in = new FileInputStream(file);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line.split(" ")[0].split("@")[0]);
            }
//            System.out.println(lines.size());
        }

        Set<String> uniqueLines = new HashSet<>(lines);
//        System.out.println(uniqueLines.size());

        return uniqueLines;
    }

    public static List<String> readFile(File file) throws IOException {
        InputStream in = new FileInputStream(file);
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines;
    }
}
