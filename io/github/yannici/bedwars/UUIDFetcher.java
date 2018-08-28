package io.github.yannici.bedwars;

import com.google.common.collect.ImmutableList;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class UUIDFetcher
  implements Callable<Map<String, UUID>>
{
  private static final double PROFILES_PER_REQUEST = 100.0D;
  private static final String PROFILE_URL = "https://api.mojang.com/profiles/minecraft";
  private final JSONParser jsonParser = new JSONParser();
  private final List<String> names;
  private final boolean rateLimiting;
  
  public UUIDFetcher(List<String> paramList, boolean paramBoolean)
  {
    this.names = ImmutableList.copyOf(paramList);
    this.rateLimiting = paramBoolean;
  }
  
  public UUIDFetcher(List<String> paramList)
  {
    this(paramList, true);
  }
  
  public Map<String, UUID> call()
    throws Exception
  {
    HashMap localHashMap = new HashMap();
    int i = (int)Math.ceil(this.names.size() / 100.0D);
    for (int j = 0; j < i; j++)
    {
      HttpURLConnection localHttpURLConnection = createConnection();
      String str1 = JSONArray.toJSONString(this.names.subList(j * 100, Math.min((j + 1) * 100, this.names.size())));
      writeBody(localHttpURLConnection, str1);
      JSONArray localJSONArray = (JSONArray)this.jsonParser.parse(new InputStreamReader(localHttpURLConnection.getInputStream()));
      Iterator localIterator = localJSONArray.iterator();
      while (localIterator.hasNext())
      {
        Object localObject = localIterator.next();
        JSONObject localJSONObject = (JSONObject)localObject;
        String str2 = (String)localJSONObject.get("id");
        String str3 = (String)localJSONObject.get("name");
        UUID localUUID = getUUID(str2);
        localHashMap.put(str3, localUUID);
      }
      if ((this.rateLimiting) && (j != i - 1)) {
        Thread.sleep(100L);
      }
    }
    return localHashMap;
  }
  
  private static void writeBody(HttpURLConnection paramHttpURLConnection, String paramString)
    throws Exception
  {
    OutputStream localOutputStream = paramHttpURLConnection.getOutputStream();
    localOutputStream.write(paramString.getBytes());
    localOutputStream.flush();
    localOutputStream.close();
  }
  
  private static HttpURLConnection createConnection()
    throws Exception
  {
    URL localURL = new URL("https://api.mojang.com/profiles/minecraft");
    HttpURLConnection localHttpURLConnection = (HttpURLConnection)localURL.openConnection();
    localHttpURLConnection.setRequestMethod("POST");
    localHttpURLConnection.setRequestProperty("Content-Type", "application/json");
    localHttpURLConnection.setUseCaches(false);
    localHttpURLConnection.setDoInput(true);
    localHttpURLConnection.setDoOutput(true);
    return localHttpURLConnection;
  }
  
  private static UUID getUUID(String paramString)
  {
    return UUID.fromString(paramString.substring(0, 8) + "-" + paramString.substring(8, 12) + "-" + paramString.substring(12, 16) + "-" + paramString.substring(16, 20) + "-" + paramString.substring(20, 32));
  }
  
  public static byte[] toBytes(UUID paramUUID)
  {
    ByteBuffer localByteBuffer = ByteBuffer.wrap(new byte[16]);
    localByteBuffer.putLong(paramUUID.getMostSignificantBits());
    localByteBuffer.putLong(paramUUID.getLeastSignificantBits());
    return localByteBuffer.array();
  }
  
  public static UUID fromBytes(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte.length != 16) {
      throw new IllegalArgumentException("Illegal byte array length: " + paramArrayOfByte.length);
    }
    ByteBuffer localByteBuffer = ByteBuffer.wrap(paramArrayOfByte);
    long l1 = localByteBuffer.getLong();
    long l2 = localByteBuffer.getLong();
    return new UUID(l1, l2);
  }
  
  public static UUID getUUIDOf(String paramString)
    throws Exception
  {
    return (UUID)new UUIDFetcher(Arrays.asList(new String[] { paramString })).call().get(paramString);
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.UUIDFetcher
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */