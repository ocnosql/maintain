package com.ailk.util;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogConfigurationException;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.impl.SimpleLog;

public class PropertiesUtil {
	private static Map<String, Properties> propsCache = new HashMap<String, Properties>();
	private static Log logger = LogFactory.getLog(PropertiesUtil.class);

	@SuppressWarnings("static-access")
	public static Properties getProperties(String resourceName) {
		Properties props = propsCache.get(resourceName);
		if (props == null) {
			PropertiesUtil utilProperties = new PropertiesUtil();
			props = new Properties();
			ClassLoader classLoader = getContextClassLoader();
			InputStream in = null;

			try {
				in = classLoader.getSystemResourceAsStream(resourceName);
				props.load(in);
			} catch (Exception e) {
				try {
					in = utilProperties.getClass().getResourceAsStream(
							"/" + resourceName);
					props.load(in);
				} catch (Exception ex) {
					throw new RuntimeException(" Couldn't find the URL: " + resourceName, ex);
				}
			} finally {
				if(in != null) {
					try {
						in.close();
					} catch (IOException e) {
						logger.error("close inputstream exception", e);
					}
				}
			}
			propsCache.put(resourceName, props);
		}
		return props;
	}

	public static String getProperty(String resourceName, String key,
			String defaultValue) {
		Properties props = getProperties(resourceName);
		return props.getProperty(key, defaultValue);
	}

	public static String getProperty(String resourceName, String key) {
		return getProperty(resourceName, key, "");
	}

	public static Map<String, String> getMatchPropertys(String resourceName,
			String inkey) {
		Map<String, String> result = new HashMap<String, String>();
		Properties props = propsCache.get(resourceName);
		PropertiesUtil utilProperties = new PropertiesUtil();
		if (props == null) {
			props = new Properties();
			URL url = ClassLoader.getSystemResource(resourceName);
			InputStream in = null;

			try {
				in = url.openStream();
				props.load(in);

			} 
			catch (Exception e) {
				try {
					in = utilProperties.getClass().getResourceAsStream(
							"/" + resourceName);
					props.load(in);
				} 
				catch (Exception e1) {
					logger.error(" Couldn't find the URL: " + resourceName + e1);
					return result;
				}
			}
		}
		Enumeration<?> en = props.propertyNames();
		while (en.hasMoreElements()) {
			String key = (String) en.nextElement();
			if (StringUtils.contains(key, inkey)) {
				result.put(key, props.getProperty(key, ""));
			}
		}
		return result;
	}
	
	/**
     * Return the thread context class loader if available.
     * Otherwise return null.
     *
     * The thread context class loader is available for JDK 1.2
     * or later, if certain security conditions are met.
     *
     * @exception LogConfigurationException if a suitable class loader
     * cannot be identified.
     */
	private static ClassLoader getContextClassLoader()
    {
        ClassLoader classLoader = null;

        if (classLoader == null) {
            try {
                // Are we running on a JDK 1.2 or later system?
                Method method = Thread.class.getMethod("getContextClassLoader", null);

                // Get the thread context class loader (if there is one)
                try {
                    classLoader = (ClassLoader)method.invoke(Thread.currentThread(), null);
                } catch (IllegalAccessException e) {
                    ; // ignore
                } catch (InvocationTargetException e) {
                    /**
                     * InvocationTargetException is thrown by 'invoke' when
                     * the method being invoked (getContextClassLoader) throws
                     * an exception.
                     *
                     * getContextClassLoader() throws SecurityException when
                     * the context class loader isn't an ancestor of the
                     * calling class's class loader, or if security
                     * permissions are restricted.
                     *
                     * In the first case (not related), we want to ignore and
                     * keep going. We cannot help but also ignore the second
                     * with the logic below, but other calls elsewhere (to
                     * obtain a class loader) will trigger this exception where
                     * we can make a distinction.
                     */
                    if (e.getTargetException() instanceof SecurityException) {
                        ; // ignore
                    } else {
                        // Capture 'e.getTargetException()' exception for details
                        // alternate: log 'e.getTargetException()', and pass back 'e'.
                        throw new LogConfigurationException
                            ("Unexpected InvocationTargetException", e.getTargetException());
                    }
                }
            } catch (NoSuchMethodException e) {
                // Assume we are running on JDK 1.1
                ; // ignore
            }
        }

        if (classLoader == null) {
            classLoader = SimpleLog.class.getClassLoader();
        }

        // Return the selected class loader
        return classLoader;
    }

//    private static InputStream getResourceAsStream(final String name)
//    {
//        return (InputStream)AccessController.doPrivileged(
//            new PrivilegedAction() {
//                public Object run() {
//                    ClassLoader threadCL = getContextClassLoader();
//
//                    if (threadCL != null) {
//                        return threadCL.getResourceAsStream(name);
//                    } else {
//                        return ClassLoader.getSystemResourceAsStream(name);
//                    }
//                }
//            });
//    }
}
