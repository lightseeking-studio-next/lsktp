package net.minecraft.resources;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.lang.reflect.Type;
import javax.annotation.Nullable;
import net.minecraft.ResourceLocationException;
import net.minecraft.network.chat.Component;
import net.minecraft.util.GsonHelper;
import org.apache.commons.lang3.StringUtils;

public class ResourceLocation implements Comparable<ResourceLocation> {
   public static final Codec<ResourceLocation> CODEC = Codec.STRING.comapFlatMap(ResourceLocation::read, ResourceLocation::toString).stable();
   private static final SimpleCommandExceptionType ERROR_INVALID = new SimpleCommandExceptionType(Component.translatable("argument.id.invalid"));
   public static final char NAMESPACE_SEPARATOR = ':';
   public static final String DEFAULT_NAMESPACE = "minecraft";
   public static final String REALMS_NAMESPACE = "realms";
   protected final String namespace;
   protected final String path;

   protected ResourceLocation(String[] p_135814_) {
      this.namespace = StringUtils.isEmpty(p_135814_[0]) ? "minecraft" : p_135814_[0];
      this.path = p_135814_[1];
      if (!isValidNamespace(this.namespace)) {
         throw new ResourceLocationException("Non [a-z0-9_.-] character in namespace of location: " + this.namespace + ":" + this.path);
      } else if (!isValidPath(this.path)) {
         throw new ResourceLocationException("Non [a-z0-9/._-] character in path of location: " + this.namespace + ":" + this.path);
      }
   }

   /** @deprecated Forge: Consider using {@link #parse(String)} instead, as Mojang made this constructor private in 1.21 */
   @Deprecated(forRemoval = true, since = "1.20.6")
   public ResourceLocation(String p_135809_) {
      this(decompose(p_135809_, ':'));
   }

   /** @deprecated Forge: Consider using {@link #fromNamespaceAndPath(String, String)} instead, as Mojang made this constructor private in 1.21 */
   @Deprecated(forRemoval = true, since = "1.20.6")
   public ResourceLocation(String p_135811_, String p_135812_) {
      this(new String[]{p_135811_, p_135812_});
   }

   /** @deprecated Forge: Consider using {@link #bySeparator(String, char)} instead, as Mojang removed this method in 1.21 */
   @Deprecated(forRemoval = true, since = "1.20.6")
   public static ResourceLocation of(String p_135823_, char p_135824_) {
      return new ResourceLocation(decompose(p_135823_, p_135824_));
   }

   @Nullable
   public static ResourceLocation tryParse(String p_135821_) {
      try {
         return new ResourceLocation(p_135821_);
      } catch (ResourceLocationException resourcelocationexception) {
         return null;
      }
   }

   @Nullable
   public static ResourceLocation tryBuild(String p_214294_, String p_214295_) {
      try {
         return new ResourceLocation(p_214294_, p_214295_);
      } catch (ResourceLocationException resourcelocationexception) {
         return null;
      }
   }

   protected static String[] decompose(String p_135833_, char p_135834_) {
      String[] astring = new String[]{"minecraft", p_135833_};
      int i = p_135833_.indexOf(p_135834_);
      if (i >= 0) {
         astring[1] = p_135833_.substring(i + 1, p_135833_.length());
         if (i >= 1) {
            astring[0] = p_135833_.substring(0, i);
         }
      }

      return astring;
   }

   public static DataResult<ResourceLocation> read(String p_135838_) {
      try {
         return DataResult.success(new ResourceLocation(p_135838_));
      } catch (ResourceLocationException resourcelocationexception) {
         return DataResult.error("Not a valid resource location: " + p_135838_ + " " + resourcelocationexception.getMessage());
      }
   }

   public String getPath() {
      return this.path;
   }

   public String getNamespace() {
      return this.namespace;
   }

   public String toString() {
      return this.namespace + ":" + this.path;
   }

   public boolean equals(Object p_135846_) {
      if (this == p_135846_) {
         return true;
      } else if (!(p_135846_ instanceof ResourceLocation)) {
         return false;
      } else {
         ResourceLocation resourcelocation = (ResourceLocation)p_135846_;
         return this.namespace.equals(resourcelocation.namespace) && this.path.equals(resourcelocation.path);
      }
   }

   public int hashCode() {
      return 31 * this.namespace.hashCode() + this.path.hashCode();
   }

   public int compareTo(ResourceLocation p_135826_) {
      int i = this.path.compareTo(p_135826_.path);
      if (i == 0) {
         i = this.namespace.compareTo(p_135826_.namespace);
      }

      return i;
   }

   // Normal compare sorts by path first, this compares namespace first.
   public int compareNamespaced(ResourceLocation o) {
      int ret = this.namespace.compareTo(o.namespace);
      return ret != 0 ? ret : this.path.compareTo(o.path);
   }

   public String toDebugFileName() {
      return this.toString().replace('/', '_').replace(':', '_');
   }

   public String toLanguageKey() {
      return this.namespace + "." + this.path;
   }

   public String toShortLanguageKey() {
      return this.namespace.equals("minecraft") ? this.path : this.toLanguageKey();
   }

   public String toLanguageKey(String p_214297_) {
      return p_214297_ + "." + this.toLanguageKey();
   }

   public static ResourceLocation read(StringReader p_135819_) throws CommandSyntaxException {
      int i = p_135819_.getCursor();

      while(p_135819_.canRead() && isAllowedInResourceLocation(p_135819_.peek())) {
         p_135819_.skip();
      }

      String s = p_135819_.getString().substring(i, p_135819_.getCursor());

      try {
         return new ResourceLocation(s);
      } catch (ResourceLocationException resourcelocationexception) {
         p_135819_.setCursor(i);
         throw ERROR_INVALID.createWithContext(p_135819_);
      }
   }

   public static boolean isAllowedInResourceLocation(char p_135817_) {
      return p_135817_ >= '0' && p_135817_ <= '9' || p_135817_ >= 'a' && p_135817_ <= 'z' || p_135817_ == '_' || p_135817_ == ':' || p_135817_ == '/' || p_135817_ == '.' || p_135817_ == '-';
   }

   public static boolean isValidPath(String p_135842_) {
      for(int i = 0; i < p_135842_.length(); ++i) {
         if (!validPathChar(p_135842_.charAt(i))) {
            return false;
         }
      }

      return true;
   }

   public static boolean isValidNamespace(String p_135844_) {
      for(int i = 0; i < p_135844_.length(); ++i) {
         if (!validNamespaceChar(p_135844_.charAt(i))) {
            return false;
         }
      }

      return true;
   }

   public static boolean validPathChar(char p_135829_) {
      return p_135829_ == '_' || p_135829_ == '-' || p_135829_ >= 'a' && p_135829_ <= 'z' || p_135829_ >= '0' && p_135829_ <= '9' || p_135829_ == '/' || p_135829_ == '.';
   }

   public static boolean validNamespaceChar(char p_135836_) {
      return p_135836_ == '_' || p_135836_ == '-' || p_135836_ >= 'a' && p_135836_ <= 'z' || p_135836_ >= '0' && p_135836_ <= '9' || p_135836_ == '.';
   }

   public static boolean isValidResourceLocation(String p_135831_) {
      String[] astring = decompose(p_135831_, ':');
      return isValidNamespace(StringUtils.isEmpty(astring[0]) ? "minecraft" : astring[0]) && isValidPath(astring[1]);
   }

   public static class Serializer implements JsonDeserializer<ResourceLocation>, JsonSerializer<ResourceLocation> {
      public ResourceLocation deserialize(JsonElement p_135851_, Type p_135852_, JsonDeserializationContext p_135853_) throws JsonParseException {
         return new ResourceLocation(GsonHelper.convertToString(p_135851_, "location"));
      }

      public JsonElement serialize(ResourceLocation p_135855_, Type p_135856_, JsonSerializationContext p_135857_) {
         return new JsonPrimitive(p_135855_.toString());
      }
   }

   // FORGE: BACKPORTS FROM 1.21

   /** Forge: This is a backported method from 1.21, and is the replacement for {@link #ResourceLocation(String, String)}. */
   public static ResourceLocation fromNamespaceAndPath(String namespace, String path) {
      return new ResourceLocation(namespace, path);
   }

   /** Forge: This is a backported method from 1.21, and is the replacement for {@link #ResourceLocation(String)}. */
   public static ResourceLocation parse(String location) {
      return new ResourceLocation(location);
   }

   /**
    * Forge: This is a backported method from 1.21, and acts as an alternative to using {@link #parse(String)}. If you
    * know for sure you are going to be using the {@linkplain #DEFAULT_NAMESPACE default namespace}
    * ({@code "minecraft"}), use this.
    */
   public static ResourceLocation withDefaultNamespace(String path) {
      return new ResourceLocation(DEFAULT_NAMESPACE, assertValidPath(DEFAULT_NAMESPACE, path), null);
   }

   /** Forge: This is a backported method from 1.21, and is the replacement for {@link #of(String, char)}. */
   public static ResourceLocation bySeparator(String location, char separator) {
      return of(location, separator);
   }

   /**
    * Forge: This is a backported method from 1.21, and is the same as {@link #bySeparator(String, char)} but returns
    * {@code null} if a resource location cannot be created.
    */
   public static @Nullable ResourceLocation tryBySeparator(String location, char separator) {
      int i = location.indexOf(separator);
      if (i >= 0) {
         String s = location.substring(i + 1);
         if (!isValidPath(s)) {
            return null;
         } else if (i != 0) {
            String s1 = location.substring(0, i);
            return isValidNamespace(s1) ? new ResourceLocation(s1, s, null) : null;
         } else {
            return new ResourceLocation(DEFAULT_NAMESPACE, s, null);
         }
      } else {
         return isValidPath(location) ? new ResourceLocation(DEFAULT_NAMESPACE, location, null) : null;
      }
   }

   // FORGE: BACKPORTS FROM 1.19.3

   /** Forge: Dummy for {@link #ResourceLocation(String, String, ForgeDummy)} */
   private interface ForgeDummy { }

   /** Forge: Backported constructor from 1.19.3, which skips additional validations. */
   private ResourceLocation(String namespace, String path, @Nullable ForgeDummy dummy) {
      this.namespace = namespace;
      this.path = path;
   }

   /** Forge: Backported method from 1.19.3 to handle path assertion without needing to use the constructor. */
   private static String assertValidPath(String namespace, String path) {
      if (!isValidPath(path)) {
         throw new ResourceLocationException("Non [a-z0-9/._-] character in path of location: " + namespace + ":" + path);
      } else {
         return path;
      }
   }

   /** Forge: This is a backported method from 1.19.3, can be used to quickly create a new resource location with a different path. */
   public ResourceLocation withPath(String path) {
      return new ResourceLocation(this.namespace, assertValidPath(this.namespace, path), null);
   }

   /** Forge: This is a backported method from 1.19.3, can be used to quickly create a new resource location with a modified path. */
   public ResourceLocation withPath(java.util.function.UnaryOperator<String> pathFunction) {
      return this.withPath(pathFunction.apply(this.path));
   }

   /** Forge: This is a backported method from 1.19.3, can be used to quickly create a new resource location with a prefixed path. */
   public ResourceLocation withPrefix(String prefix) {
      return this.withPath(prefix + this.path);
   }

   /** Forge: This is a backported method from 1.19.3, can be used to quickly create a new resource location with a suffixed path. */
   public ResourceLocation withSuffix(String suffix) {
      return this.withPath(this.path + suffix);
   }

   /** Forge: This is a backported method from 1.19.3, can be used to quickly create a language key with a prefix and a suffix. */
   public String toLanguageKey(String prefix, String suffix) {
      return this.toLanguageKey(prefix) + "." + suffix;
   }
}
