import com.crazychat.chat.ChatApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@org.springframework.boot.test.context.SpringBootTest(classes = {ChatApplication.class})
public class SpringBootTest {

    @Test
    public void test() {
        System.out.println(Math.random());
    }
}
