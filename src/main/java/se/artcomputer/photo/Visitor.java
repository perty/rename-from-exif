package se.artcomputer.photo;

import java.io.File;

public interface Visitor {

    void process(File file);
}
