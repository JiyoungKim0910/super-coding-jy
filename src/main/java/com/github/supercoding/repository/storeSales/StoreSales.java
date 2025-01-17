package com.github.supercoding.repository.storeSales;

import com.github.supercoding.repository.Items.ItemEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString
@Builder
@Entity
@Table(name = "store_sales")
public class StoreSales {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "store_name", length = 30)
    private String storeName;
    @Column(name = "amount", nullable = false, columnDefinition = "DEFAULT 0 CHECK(amount) >= 0")
    private Integer amount;
    //양방향 구현
    @OneToMany(mappedBy = "storeSales", fetch = FetchType.EAGER) //Eager 로 바꾸니 findall수행 시 N+1 문제 발생 -> JPQL 로 해결
    private List<ItemEntity> itemEntities ;

}
