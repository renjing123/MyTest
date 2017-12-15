package cc.joymaker.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.reflect.AnnotatedElement;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;


/**
 * @author pautcher
 * @date 2014-12-31
 * 
 */
@Component("jsonUtils")
public class JsonUtils {
	final private static Logger log = LoggerFactory.getLogger(JsonUtils.class);

	private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	private static final XmlMapper xmlMapper;
	private static final ObjectMapper mapper;

	public ObjectMapper getMapper() {
		return mapper;
	}

	static {
		
		mapper = new ObjectMapper();
		mapper.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		mapper.setDateFormat(new SimpleDateFormat(DEFAULT_DATE_FORMAT));
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		//mapper.setSerializationInclusion(Include.NON_EMPTY);

		mapper.setAnnotationIntrospector(new JacksonAnnotationIntrospector() {
			@Override
			public Object findSerializer(Annotated a) {
				if (a instanceof AnnotatedMethod) {
					AnnotatedElement m = a.getAnnotated();
					DateTimeFormat an = m.getAnnotation(DateTimeFormat.class);
					if (an != null) {
						if (!DEFAULT_DATE_FORMAT.equals(an.pattern())) {
							return new JsonDateSerializer(an.pattern());
						}
					}
				}
				return super.findSerializer(a);
			}
		});

		xmlMapper = new XmlMapper();
	}

	public static JsonNode getNode(String json) {
		try {
			return mapper.readTree(json);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static ObjectNode getObjectNode(String json) {
		try {
			return mapper.readValue(json, ObjectNode.class);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static <T> T toBean(String json, Class<T> clazz) {
		try {
			return mapper.readValue(json, clazz);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static <T> T toBean(InputStream in, Class<T> clazz) throws IOException {
		try {
			return mapper.readValue(in, clazz);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String formBean(Object obj) {
		try {
			return mapper.writeValueAsString(obj);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> toMap(String json) {
		return toBean(json, Map.class);
	}

	public static String writeValueAsString(Object object) {
		String value = "";
		try {
			value = mapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			log.error(e.getMessage(), e);
			return StringUtils.EMPTY;
		}
		return value;
	}

	public static Map<String, Object> readValueAsObject(String value) {
		Map<String, Object> map;
		try {
			map = mapper.readValue(value, new TypeReference<Map<String, Object>>() {
			});
		} catch (JsonProcessingException e) {
			log.error(e.getMessage(), e);
			return Collections.emptyMap();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			return Collections.emptyMap();
		}
		return map;
	}

	public static Map<String, JsonNode> readValueAsJson(String value) {
		Map<String, JsonNode> map;
		try {
			map = mapper.readValue(value, new TypeReference<Map<String, JsonNode>>() {
			});
		} catch (JsonProcessingException e) {
			log.error(e.getMessage(), e);
			return Collections.emptyMap();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			return Collections.emptyMap();
		}
		return map;
	}

	public static <T> T readValue(String json, String path, Class<T> clazz) {
		JsonNode node = JsonUtils.getNode(json);
		return readValue(node, clazz);
	}

	public static <T> T readValue(JsonNode value, Class<T> clazz) {
		try {
			return mapper.readValue(value.traverse(), clazz);
		} catch (JsonProcessingException e) {
			log.error(e.getMessage(), e);
			return null;
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			return null;
		}
	}

	public static <T> T readValue(String json, TypeReference<T> typeReference) {
		try {
			return mapper.readValue(json, typeReference);
		} catch (JsonProcessingException e) {
			log.error(e.getMessage(), e);
			return null;
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			return null;
		}
	}

	public static <T> T readValue(JsonNode value, TypeReference<T> typeReference) {
		try {
			return mapper.readValue(value.traverse(), typeReference);
		} catch (JsonProcessingException e) {
			log.error(e.getMessage(), e);
			return null;
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			return null;
		}
	}

	public static class JsonDateSerializer extends JsonSerializer<Date> {
		private SimpleDateFormat dateFormat;

		public JsonDateSerializer(String format) {
			dateFormat = new SimpleDateFormat(format);
		}

		@Override
		public void serialize(Date date, JsonGenerator gen, SerializerProvider provider) throws IOException, JsonProcessingException {
			String value = dateFormat.format(date);
			gen.writeString(value);
		}
	}

	public static String xml2Json(String xml) {
		try {
			StringWriter w = new StringWriter();
			JsonParser jp = xmlMapper.getFactory().createParser(xml);
			JsonGenerator jg = mapper.getFactory().createGenerator(w);

			while (jp.nextToken() != null) {
				jg.copyCurrentEvent(jp);
			}
			jp.close();
			jg.close();
			return w.toString();
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			ex.printStackTrace();
			return null;
		}
	}
	
	public static ObjectNode createNode() {
		return mapper.createObjectNode();
	}
	
	public static ArrayNode createArray() {
		return mapper.createArrayNode();
	}
	
	public static <T> T convertValue(Object o, Class<T> t) {
		return mapper.convertValue(o, t);
	}
	

	public static void main(String[] args) {

		String xml = "<xml><ToUserName><![CDATA[toUser]]></ToUserName><FromUserName><![CDATA[FromUser]]></FromUserName><CreateTime>123456789</CreateTime><MsgType><![CDATA[event]]></MsgType><Event><![CDATA[SCAN]]></Event><EventKey><![CDATA[SCENE_VALUE]]></EventKey><Ticket><![CDATA[TICKET]]></Ticket></xml>";
		String json = JsonUtils.xml2Json(xml);
		System.out.println(json);
	}
	
}
