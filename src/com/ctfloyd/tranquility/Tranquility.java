package com.ctfloyd.tranquility;

import com.ctfloyd.tranquility.lib.runtime.Runtime;
import com.ctfloyd.tranquility.lib.runtime.Script;

import java.nio.file.Files;
import java.nio.file.Paths;

public class Tranquility {

    public static void main(String[] args) throws Exception {
        String fileContents = Files.readString(Paths.get("input/test.js"));
        Runtime runtime = new Runtime();
        Script script = Script.parseScript(fileContents.toCharArray(), runtime.getRealm(), runtime);
        script.dump();
        script.evaluate();
    }

}
