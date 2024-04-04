package bytebuddy;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.matcher.ElementMatchers;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;

import java.util.Set;

public class ByteBuddyTest {

    @Test
    public void createClassTest() throws InstantiationException, IllegalAccessException, ClassNotFoundException {

        Class<?> dynamicType = new ByteBuddy()
                .subclass(Class.forName("java.lang.Object"))
                .name("com.HelloWorld")
                .method(ElementMatchers.named("toString"))
                .intercept(FixedValue.value("Hello World!"))
                .make()
                .load(getClass().getClassLoader())

                .getLoaded();

        System.out.println(dynamicType.newInstance().getClass().getName());
        System.out.println(dynamicType.newInstance().getClass().getPackage());
        System.out.println(dynamicType.newInstance().getClass().getSimpleName());
        System.out.println(dynamicType.newInstance().toString());


        ;
        System.out.println(ClassPath.from(ClassLoader.getSystemClassLoader())
                .getAllClasses()
                .stream()
                .filter(clazz -> clazz.getPackageName()
                        .equalsIgnoreCase(packageName))
                .map(clazz -> clazz.load())
                .collect(Collectors.toSet()));
    }




}
