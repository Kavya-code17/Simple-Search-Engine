package org.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashSet;

public class Crawler {
    HashSet<String> urlSet;
    int MAX_DEPTH = 2;
    Crawler(){
        urlSet = new HashSet<String>();
    }
    public void getPagesTextsAndLinks(String url, int depth) {
        if (urlSet.contains(url)) {
            return;
        }
        if (depth >= MAX_DEPTH) {
            return;
        }
        if(urlSet.add(url)){
            System.out.println(url);
        }
        depth++;

        try {
            // Jsoup converts url(html file) to java document object
            Document document = Jsoup.connect(url).timeout(5000).get();

            // Indexer work starts here
            // Indexer saves java objects to database
            Indexer indexer = new Indexer(document, url);

            System.out.println(document.title());
            // To process the files of data
            Elements availableLinksOnPage = document.select("a[href]");
            for (Element currentLink : availableLinksOnPage) {
                // currentLink is java object so in order to convert java object to string object attribute key is used
                // Getting absolute link
                getPagesTextsAndLinks(currentLink.attr("abs:href"), depth);
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
        public static void main(String[] args) {
        // Crawler is a bot
        Crawler crawler = new Crawler();
        crawler.getPagesTextsAndLinks("http://www.javatpoint.com", 0 );
    }
}