package wang.xiaoluobo.jdk8.juc.forkjoin;/* ......................................................................................... */

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * @see <a href="https://www.oracle.com/technetwork/cn/articles/java/fork-join-422606-zhs.html">https://www.oracle.com/technetwork/cn/articles/java/fork-join-422606-zhs.html</a>
 */
/* ......................................................................................... */

class Document {
    private final List<String> lines;

    Document(List<String> lines) {
        this.lines = lines;
    }

    List<String> getLines() {
        return this.lines;
    }

    static Document fromFile(File file) throws IOException {
        List<String> lines = new LinkedList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            while (line != null) {
                lines.add(line);
                line = reader.readLine();
            }
        }
        return new Document(lines);
    }
}

/* ......................................................................................... */

class Folder {
    private final List<Folder> subFolders;
    private final List<Document> documents;

    Folder(List<Folder> subFolders, List<Document> documents) {
        this.subFolders = subFolders;
        this.documents = documents;
    }

    List<Folder> getSubFolders() {
        return this.subFolders;
    }

    List<Document> getDocuments() {
        return this.documents;
    }

    static Folder fromDirectory(File dir) throws IOException {
        List<Document> documents = new LinkedList<>();
        List<Folder> subFolders = new LinkedList<>();
        for (File entry : dir.listFiles()) {
            if (entry.isDirectory()) {
                subFolders.add(Folder.fromDirectory(entry));
            } else {
                documents.add(Document.fromFile(entry));
            }
        }
        return new Folder(subFolders, documents);
    }
}

/* ......................................................................................... */

public class WordCounter {

    /* ......................................................................................... */

    String[] wordsIn(String line) {
        return line.trim().split("(\\s|\\p{Punct})+");
    }

    Long occurrencesCount(Document document, String searchedWord) {
        long count = 0;
        for (String line : document.getLines()) {
            for (String word : wordsIn(line)) {
                if (searchedWord.equals(word)) {
                    count = count + 1;
                }
            }
        }
        return count;
    }

    /* ......................................................................................... */

    Long countOccurrencesOnSingleThread(Folder folder, String searchedWord) {
        long count = 0;
        for (Folder subFolder : folder.getSubFolders()) {
            count = count + countOccurrencesOnSingleThread(subFolder, searchedWord);
        }
        for (Document document : folder.getDocuments()) {
            count = count + occurrencesCount(document, searchedWord);
        }
        return count;
    }

    /* ......................................................................................... */

    class DocumentSearchTask extends RecursiveTask<Long> {
        private final Document document;
        private final String searchedWord;

        DocumentSearchTask(Document document, String searchedWord) {
            super();
            this.document = document;
            this.searchedWord = searchedWord;
        }

        @Override
        protected Long compute() {
            return occurrencesCount(document, searchedWord);
        }
    }

    /* ......................................................................................... */

    class FolderSearchTask extends RecursiveTask<Long> {
        private final Folder folder;
        private final String searchedWord;

        FolderSearchTask(Folder folder, String searchedWord) {
            super();
            this.folder = folder;
            this.searchedWord = searchedWord;
        }

        @Override
        protected Long compute() {
            long count = 0L;
            List<RecursiveTask<Long>> forks = new LinkedList<>();
            for (Folder subFolder : folder.getSubFolders()) {
                FolderSearchTask task = new FolderSearchTask(subFolder, searchedWord);
                forks.add(task);
                task.fork();
            }
            for (Document document : folder.getDocuments()) {
                DocumentSearchTask task = new DocumentSearchTask(document, searchedWord);
                forks.add(task);
                task.fork();
            }
            for (RecursiveTask<Long> task : forks) {
                count = count + task.join();
            }
            return count;
        }
    }

    /* ......................................................................................... */

    private final ForkJoinPool forkJoinPool = new ForkJoinPool();

    Long countOccurrencesInParallel(Folder folder, String searchedWord) {
        return forkJoinPool.invoke(new FolderSearchTask(folder, searchedWord));
    }

    /* ......................................................................................... */

    /**
     * 1. /Users/dongzai1005/soft/hadoop-2.8.4/logs hadoop 10
     * 6936 , single thread search took 357ms
     * 6936 , single thread search took 169ms
     * 6936 , single thread search took 102ms
     * 6936 , single thread search took 83ms
     * 6936 , single thread search took 64ms
     * 6936 , single thread search took 83ms
     * 6936 , single thread search took 75ms
     * 6936 , single thread search took 59ms
     * 6936 , single thread search took 53ms
     * 6936 , single thread search took 64ms
     * 6936 , fork / join search took 31ms
     * 6936 , fork / join search took 22ms
     * 6936 , fork / join search took 27ms
     * 6936 , fork / join search took 26ms
     * 6936 , fork / join search took 22ms
     * 6936 , fork / join search took 27ms
     * 6936 , fork / join search took 30ms
     * 6936 , fork / join search took 31ms
     * 6936 , fork / join search took 27ms
     * 6936 , fork / join search took 25ms
     * <p>
     * CSV Output:
     * <p>
     * Single thread,Fork/Join
     * 357,31
     * 169,22
     * 102,27
     * 83,26
     * 64,22
     * 83,27
     * 75,30
     * 59,31
     * 53,27
     * 64,25
     *
     * 2. /Users/dongzai1005/soft/hadoop-2.8.4/logs hadoop 10
     * 6936 , single thread search took 162ms
     * 6936 , single thread search took 108ms
     * 6936 , single thread search took 63ms
     * 6936 , single thread search took 47ms
     * 6936 , single thread search took 45ms
     * 6936 , single thread search took 28ms
     * 6936 , single thread search took 25ms
     * 6936 , single thread search took 26ms
     * 6936 , single thread search took 25ms
     * 6936 , single thread search took 25ms
     * 6936 , single thread search took 29ms
     * 6936 , single thread search took 32ms
     * 6936 , single thread search took 25ms
     * 6936 , single thread search took 26ms
     * 6936 , single thread search took 31ms
     * 6936 , single thread search took 38ms
     * 6936 , single thread search took 34ms
     * 6936 , single thread search took 36ms
     * 6936 , single thread search took 32ms
     * 6936 , single thread search took 25ms
     * 6936 , fork / join search took 23ms
     * 6936 , fork / join search took 15ms
     * 6936 , fork / join search took 17ms
     * 6936 , fork / join search took 13ms
     * 6936 , fork / join search took 13ms
     * 6936 , fork / join search took 13ms
     * 6936 , fork / join search took 17ms
     * 6936 , fork / join search took 12ms
     * 6936 , fork / join search took 14ms
     * 6936 , fork / join search took 21ms
     * 6936 , fork / join search took 29ms
     * 6936 , fork / join search took 29ms
     * 6936 , fork / join search took 29ms
     * 6936 , fork / join search took 22ms
     * 6936 , fork / join search took 17ms
     * 6936 , fork / join search took 13ms
     * 6936 , fork / join search took 14ms
     * 6936 , fork / join search took 13ms
     * 6936 , fork / join search took 14ms
     * 6936 , fork / join search took 64ms
     *
     * CSV Output:
     *
     * Single thread,Fork/Join
     * 162,23
     * 108,15
     * 63,17
     * 47,13
     * 45,13
     * 28,13
     * 25,17
     * 26,12
     * 25,14
     * 25,21
     * 29,29
     * 32,29
     * 25,29
     * 26,22
     * 31,17
     * 38,13
     * 34,14
     * 36,13
     * 32,14
     * 25,64
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        WordCounter wordCounter = new WordCounter();
        Folder folder = Folder.fromDirectory(new File(args[0]));

        final int repeatCount = Integer.decode(args[2]);
        long counts;
        long startTime;
        long stopTime;

        long[] singleThreadTimes = new long[repeatCount];
        long[] forkedThreadTimes = new long[repeatCount];

        for (int i = 0; i < repeatCount; i++) {
            startTime = System.currentTimeMillis();
            counts = wordCounter.countOccurrencesOnSingleThread(folder, args[1]);
            stopTime = System.currentTimeMillis();
            singleThreadTimes[i] = (stopTime - startTime);
            System.out.println(counts + " , single thread search took " + singleThreadTimes[i] + "ms");
        }

        for (int i = 0; i < repeatCount; i++) {
            startTime = System.currentTimeMillis();
            counts = wordCounter.countOccurrencesInParallel(folder, args[1]);
            stopTime = System.currentTimeMillis();
            forkedThreadTimes[i] = (stopTime - startTime);
            System.out.println(counts + " , fork / join search took " + forkedThreadTimes[i] + "ms");
        }

        System.out.println("\nCSV Output:\n");
        System.out.println("Single thread,Fork/Join");
        for (int i = 0; i < repeatCount; i++) {
            System.out.println(singleThreadTimes[i] + "," + forkedThreadTimes[i]);
        }
        System.out.println();
    }
}

/* ......................................................................................... */