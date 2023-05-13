package cn.idea360.signature.wrapper;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;

/**
 * @author cuishiying
 */
public class ContentCachingRequestWrapper extends HttpServletRequestWrapper {

	private final byte[] buffer;

	public ContentCachingRequestWrapper(HttpServletRequest request) throws IOException {
		super(request);
		InputStream is = request.getInputStream();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buff = new byte[1024];
		int read;
		while ((read = is.read(buff)) > 0) {
			baos.write(buff, 0, read);
		}
		this.buffer = baos.toByteArray();
	}

	@Override
	public ServletInputStream getInputStream() {
		final ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
		return new ServletInputStream() {

			@Override
			public boolean isFinished() {
				return false;
			}

			@Override
			public boolean isReady() {
				return false;
			}

			@Override
			public void setReadListener(ReadListener readListener) {
				// document why this method is empty
			}

			@Override
			public int read() {
				return bais.read();
			}
		};
	}

	@Override
	public BufferedReader getReader() throws IOException {
		return new BufferedReader(new InputStreamReader(getInputStream()));
	}

}
