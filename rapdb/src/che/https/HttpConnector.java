package che.https;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import che.bean.ConvertedBean;
import che.panels.MainFrame;

/**
 * compose http request
 * parse http response
 * @author yongger-john
 *
 */
public class HttpConnector implements Runnable {

	public static int ii = 0;
	
	public static final String WEB_PERFIX = "http://rapdb.dna.affrc.go.jp";
	
	private String rapdbUrl = "http://rapdb.dna.affrc.go.jp/tools/converter/run";
	
	private String rapdbParam = "";
	
	private Random radom = new Random();
	
	private static Map<String, String> rapdbHeaders = new HashMap<String, String>();
	static{
		rapdbHeaders.put("Accept-Language", "zh-CN,zh;q=0.8");
		rapdbHeaders.put("Connection", "keep-alive");
		rapdbHeaders.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.118 Safari/537.36");
//		rapdbHeaders.put("Host", "rapdb.dna.affrc.go.jp");
		rapdbHeaders.put("Origin", "http://rapdb.dna.affrc.go.jp");
		rapdbHeaders.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
	}
	
	public HttpConnector(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
	}
	
	public void start() {
		Thread thread = new Thread(this);
		thread.start();
	}
	
	public void run() {
		startRequest();
	}
	
	
	MainFrame mainFrame = null;
	
	public List<ConvertedBean> startRequest() {
		mainFrame.addLog("start request:" + rapdbUrl);
		mainFrame.addLog("  request param:" + rapdbParam);
		
		try {
			String html = requestRqpdb();
			List<ConvertedBean> idconverts = parseRapdb(html);
			
			requestRapDetailAll(idconverts);
			requestRapSequenceAll(idconverts);
			
			mainFrame.connectorCallBackk(idconverts);
			mainFrame.addLog("-------------FINISH--------------------");
			return idconverts;
		} catch (Exception e) {
			mainFrame.addLog("[ERROR] system error:" + e);
//			e.printStackTrace();
		}
		return null;
	}
	
	private void requestRapSequenceAll(List<ConvertedBean> idconverts) {
		for(ConvertedBean bean : idconverts){
			requestRapSequence(bean);
		}
	}

	private void requestRapSequence(ConvertedBean bean) {
		if(bean.getRapVariantsUrl() == null || "".equals(bean.getRapVariantsUrl())){
			mainFrame.addLog("[ERROR] parse Msu[" + bean.getMsu() + "] RAP[" + bean.getRap() + "] do not get variant");
			return ;
		}
		
		String url = HttpConnector.WEB_PERFIX + bean.getRapVariantsUrl();
		try {
			Double ramdomD = 1000 * radom.nextDouble();
			Thread.sleep(ramdomD.intValue());
			Document document = Jsoup.connect(url)
				.userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.118 Safari/537.36")
						.get();
				
			Element pageElement = document.getElementById("page3");
			Element sequenceElement = pageElement.nextElementSibling().nextElementSibling();
			String sequenct = sequenceElement.text();
			bean.setRapSequence(sequenct);
//			System.out.println("   parse rapSequence[" + bean.getRap()  + "]-OK");
			mainFrame.addLog("   parse rapSequence[" + bean.getRap()  + "]-OK");
		} catch (Exception e) {
			mainFrame.addLog("[ERROR] parse rapSequence[" + bean.getRap() + "],url[" + url + "]," + e.getLocalizedMessage());
		}
	}

	private void requestRapDetailAll(List<ConvertedBean> idconverts){
		for(ConvertedBean bean : idconverts){
			requestRapDetail(bean);
		}
	}
	private void requestRapDetail(ConvertedBean bean){
		if(bean.getRapUrl() == null || "".equals(bean.getRapUrl())){
			mainFrame.addLog("[ERROR] parse Msu[" + bean.getMsu() + "]  do not get RAP");
			return ;
		}
		String url = HttpConnector.WEB_PERFIX + bean.getRapUrl();
		try {
			Double ramdomD = 1000 * radom.nextDouble();
			Thread.sleep(ramdomD.intValue());
			Document document = Jsoup.connect(url)
				.userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.118 Safari/537.36")
						.get();
			for(Element element : document.getElementsByClass("databody")){
				if(element.child(0).text().indexOf("Transcript")>=0){
					Element aTag = element.getElementsByTag("a").get(0);
					bean.setRapVariants(aTag.text());
					bean.setRapVariantsUrl(aTag.attr("href"));
					mainFrame.addLog("   parse RAP[" + bean.getRap()  + "],get Transcript[" + bean.getRapVariants() + "]");
					break;
				}
			}
		} catch (Exception e) {
			mainFrame.addLog("[ERROR] parse RAP[" + bean.getRap() + "],url[" + url + "]," + e.getLocalizedMessage());
		}
		
	}
	
	private List<ConvertedBean> parseRapdb(String html){
		List<ConvertedBean> idconverts = new ArrayList<ConvertedBean>();
		Document doc = Jsoup.parse(html);
		Element table = doc.getElementById("tools_converter");
		Elements trs = table.child(0).child(0).children();
		for(Element trElement : trs){
			if(trElement.hasClass("result")){
				ConvertedBean row = pareseRapdbTr(trElement);
				idconverts.add(row);
			}
		}
		return idconverts;
	}
	
	private ConvertedBean pareseRapdbTr(Element trElement){
		ConvertedBean result = new ConvertedBean();
		
		Elements msuTd = trElement.getElementsByClass("c01");
		String[] msu = parseRapdbTd(msuTd.get(0));
		result.setMsu(msu[0]);
		result.setMsuUrl(msu[1]);
		Elements rapTd = trElement.getElementsByClass("c02");
		String[] rap = parseRapdbTd(rapTd.get(0));
		result.setRap(rap[0]);
		result.setRapUrl(rap[1]);
//		result.put("RAP", rap);
		
//		System.out.println("  MSU (LOC_Os ID):" + msu[0] + "\t" + "RAP (Os ID):" + rap[0]);
		mainFrame.addLog("  MSU (LOC_Os ID):" + msu[0] + "\t" + "RAP (Os ID):" + rap[0]);
		return result;
	}
	
	private String[] parseRapdbTd(Element td){
		String[] result = new String[2];
		Elements aTag = td.getElementsByTag("a");
		if(aTag.size()>0){
			result[0] = aTag.get(0).text();
			result[1] = aTag.get(0).attr("href");
			return result;
		}else{
			return result;
		}
	}
	
	private String requestRqpdb() throws IOException{
		HttpURLConnection connection = createRqpdbConnection();
		String mimeBoundary = mimeBoundary();
		
		connection.addRequestProperty("Content-Type", "multipart/form-data; boundary=" + mimeBoundary);
		OutputStream output = null;
		InputStream input = null;
		try{
			String bodyStr = createRdpdbRequestBody(mimeBoundary, rapdbParam);
			connection.connect();
			output = connection.getOutputStream();
			output.write(bodyStr.getBytes());
			
			mainFrame.addLog("ID Converter response:" + connection.getResponseMessage());
			input = connection.getInputStream();
			byte[] buffer = new byte[0x20000];
			int read = 0;
			ByteArrayOutputStream outStream = new ByteArrayOutputStream(0x20000);
			while(true){
				read = input.read(buffer);
				if(read == -1){
					break;
				}
				 outStream.write(buffer, 0, read);
			}
			return new String(outStream.toByteArray());
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(output != null){
				output.close();
			}
			if(input != null){
				input.close();
			}
			connection.disconnect();
		}
		return "";
	}
	private HttpURLConnection createRqpdbConnection() throws MalformedURLException, IOException{
		rapdbUrl = "http://localhost/test";
		HttpURLConnection connection = (HttpURLConnection) (new URL(rapdbUrl)).openConnection();
		connection.setRequestMethod("POST");
		connection.setConnectTimeout(5 * 60 * 1000);
		connection.setReadTimeout(5 * 60 * 1000);
		for( Entry<String, String> header : rapdbHeaders.entrySet()){
			connection.addRequestProperty(header.getKey(), header.getValue());
		}
		connection.setDoOutput(true);
		connection.setUseCaches(false);
		return connection;
	}
	
	private String createRdpdbRequestBody(String mineBoundary, String queryText){
		StringBuffer sb = new StringBuffer();
		sb = sb.append("--").append(mineBoundary);
		sb.append("\r\n");
		sb.append("Content-Disposition: form-data; name=\"keyword\"");
		sb.append("\r\n\r\n");
		sb.append(queryText);
		sb.append("\r\n");
		
		sb.append("--").append(mineBoundary);
		sb.append("\r\n");
		sb.append("Content-Disposition: form-data; name=\"submit\"");
		sb.append("\r\n\r\n");
		sb.append("Convert");
		sb.append("\r\n");
		
		sb.append("--").append(mineBoundary).append("--").append("\r\n");
		return sb.toString();
	}
	
	private static final char[] mimeBoundaryChars =
            "-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    static final int boundaryLength = 32;
	public static String mimeBoundary() {
        final StringBuilder mime = new StringBuilder(boundaryLength);
        final Random rand = new Random();
        for (int i = 0; i < boundaryLength; i++) {
            mime.append(mimeBoundaryChars[rand.nextInt(mimeBoundaryChars.length)]);
        }
        return mime.toString();
    }

	public String getRapdbUrl() {
		return rapdbUrl;
	}

	public void setRapdbUrl(String rapdbUrl) {
		this.rapdbUrl = rapdbUrl;
	}

	public String getRapdbParam() {
		return rapdbParam;
	}

	public void setRapdbParam(String rapdbParam) {
		this.rapdbParam = rapdbParam;
	}

}
