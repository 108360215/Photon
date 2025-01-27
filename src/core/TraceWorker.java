//	The MIT License (MIT)
//	
//	Copyright (c) 2016 Tzu-Chieh Chang (as known as D01phiN)
//	
//	Permission is hereby granted, free of charge, to any person obtaining a copy
//	of this software and associated documentation files (the "Software"), to deal
//	in the Software without restriction, including without limitation the rights
//	to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
//	copies of the Software, and to permit persons to whom the Software is
//	furnished to do so, subject to the following conditions:
//	
//	The above copyright notice and this permission notice shall be included in all
//	copies or substantial portions of the Software.
//	
//	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//	IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//	FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//	AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//	LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//	OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
//	SOFTWARE.

package core;

import scene.Scene;
import util.Debug;

public class TraceWorker implements Runnable
{
	private Scene         m_scene;
	private SampleManager m_sampleManager;
	private PathTracer    m_pathTracer;
	private HdrFrame         m_sampleResult;
	
	public TraceWorker(Scene scene, SampleManager sampleManager, int xRes, int yRes)
	{
		m_scene         = scene;
		m_sampleManager = sampleManager;
		m_pathTracer    = new PathTracer();
		m_sampleResult  = new HdrFrame(xRes, yRes);
	}
	
	@Override
	public void run()
	{
		try
		{
			while(true)
			{
				m_pathTracer.trace(m_scene, m_sampleResult);
				m_sampleManager.addSample(m_sampleResult);
			}
		}
		catch(Exception e)
		{
			Debug.printErr("LALALA!!!");
			Debug.printErr(e.getMessage());
			e.printStackTrace();
			Debug.exit();
		}
	}
}
