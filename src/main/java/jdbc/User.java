package jdbc;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class User {
    private Long id;
    private String firstName;
    private String lastName;
    private int age;
}
