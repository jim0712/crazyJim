package cn.jim.demo;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class IndexManager {
	
	@Test
	public void testAdd() throws Exception {
		
	Directory directory = FSDirectory.open(new File("D:\\SSM项目\\Lucene\\资料\\lucene_index"));
	Analyzer analyzer = new StandardAnalyzer();
	IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, analyzer);
	IndexWriter indexWriter = new IndexWriter(directory,config);
	File dir = new File("D:\\SSM项目\\资料：lucene\\source");
	for(File f:dir.listFiles()) {
		String fileName = f.getName();
		String fileContent = FileUtils.readFileToString(f,"utf-8");
		String filePath = f.getPath();
		long fileSize = FileUtils.sizeOf(f);
		
		Field fileNameField = new TextField("filename",fileName,Store.YES);
		Field fileContentField = new TextField("content",fileContent,Store.YES);
		Field filePathField = new TextField("path",filePath,Store.YES);
		Field fileSizeField = new TextField("size",fileSize+"",Store.YES);
		
		Document document = new Document();
		document.add(fileNameField);
		document.add(fileContentField);
		document.add(filePathField);
		document.add(fileSizeField);
		indexWriter.addDocument(document);
	}
	indexWriter.close();
	}
	
	
	
	@Test
	public void testSearch() throws Exception {
		Directory directory = FSDirectory.open(new File("D:\\SSM项目\\Lucene\\资料\\lucene_index"));
		IndexReader indexReader = DirectoryReader.open(directory);
		IndexSearcher indexSearcher = new IndexSearcher(indexReader);
		Term term = new Term("content","spring");
		Query query = new TermQuery(term);
		TopDocs topDocs = indexSearcher.search(query, 100);
		
		System.out.println("查询出的总条数"+topDocs.totalHits);
		
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		for(ScoreDoc scoreDoc:scoreDocs) {
			int docId = scoreDoc.doc;
			Document doc = indexSearcher.doc(docId);
			System.out.println(doc.get("filename"));
			//System.out.println(doc.get("content"));
			System.out.println(doc.get("path"));
			System.out.println(doc.get("size"));
		}
		
		indexReader.close();
	}
	
	
		@Test
		public void testAnalyzer() throws Exception {
			//Analyzer analyzer = new StandardAnalyzer();
			//Analyzer analyzer = new CJKAnalyzer();
			Analyzer analyzer = new IKAnalyzer();
			//Analyzer analyzer = new SmartChineseAnalyzer();
			TokenStream tokenStream = analyzer.tokenStream("text","全文检索是将整本书他妈的java、整篇文章中的任意内容信息查找出来的检索,出自传智播客 浪里白条");
			tokenStream.reset();
			CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
			while (tokenStream.incrementToken()) {
				System.out.println(charTermAttribute);
			}
		}
}