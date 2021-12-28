package study.datajpa.reository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import study.datajpa.entity.Item;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class ItemRepositoryTest {
    @Autowired
    ItemRepository itemRepository;


    @Test
    public void save(){
        //DB에 값이 없는데 식벽자가 채번해서 넣어주는 것이기 때문에 merge 를 실행한다.
        //잘못됫다. -> 성능상 좋지 않다.

        Item item = new Item("A");

        itemRepository.save(item);

    }
}