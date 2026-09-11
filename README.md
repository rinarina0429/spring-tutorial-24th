# spring-tutorial-24th
CEOS 백엔드 24기 스프링 튜토리얼

## 1️⃣ spring이 지원하는 기술들(IoC/DI, AOP, PSA 등)을 자유롭게 조사해요

스프링에는 세가지 핵심 기술이 있습니다. 스프링 삼각형이라고도 부르는 3대 핵심 요소는 IoC/DI, AOP, PSA로, 이들은 서로 매우 긴밀하게 협력하여 동작합니다. 이들의 공통된 최종 목표는 순수한 자바 객체(POJO)를 유지하면서 결합도를 낮추고 유연한 서버 애플리케이션을 만드는 것입니다.

<img src="img/spring_triangle.png" width="30%">

근데? 사실 백번 읽어봐도 모르겠으니! 하나씩 공부해봅시다.

### ① IoC/DI

IoC/DI는 제어의 역전(Inversion of Control)과 의존성 주입(Dependency Injection)의 줄임말입니다.
Spring에서는 **IoC 컨테이너**가 객체 간의 의존성을 주입합니다.
여기서 '의존성 주입'은 '제어의 역전'의 특수한 형태입니다.
<br><br>
예시를 통해 조금 더 쉽게 살펴보도록 하겠습니다.
<br>
요리사가 국수를 요리하는 상황을 자바 코드로 비교해봅시다.

```java
public class Chef {
private PorkSpine meat;

    public Chef() {
        // 요리사가 직접 구체적인 객체를 생성합니다 (제어권이 요리사에게 있음)
        this.meat = new PorkSpine(); 
    }

    public void cook() {
        System.out.println("푹 고아낸 " + meat.getName() + " 국수 완성!");
    }
}
```
위 코드에서, 요리사는 직접 구체적인 객체를 생성합니다. 즉, 제어권이 요리사에게 있죠. 하지만 이 경우 돼지등뼈가 소진되면 어떻게 해야 할까요? 요리사가 직접 정육점까지 뛰어가 돼지등뼈를 사와야 하는 상황이 발생합니다.
<br>
하지만 요리사가 아닌, 사장님이 출근길에 고기를 사오면 훨씬 좋지 않을까요? 그리고 돼지등뼈가 없어도, 다른 고기를 넣을 수 있으면 더 편하지 않을까요?

```java
@Component
public class Chef {
    private final Meat meat;

    // 요리사는 추상적인 '고기(Meat 인터페이스)'면 뭐든 받아서 요리합니다.
    // 객체 생성과 공급의 제어권(IoC)이 스프링 사장님에게 넘어갔습니다!
    @Autowired 
    public Chef(Meat meat) {
        this.meat = meat; 
    }

    public void cook() {
        System.out.println("푹 고아낸 " + meat.getName() + " 국수 완성!");
    }
}
```
식당 사장님, 즉 스프링 컨테이너에게 제어권을 넘기면 요리사는 "고기"이기만 하면 뭐든 받아 요리하면 되는 편리한 상황이 됩니다.
또한 코드가 인터페이스에만 의존하게 되어 결합도가 낮아지죠.
<br>
위 예시 코드에서 본 방식은 생성자 주입(Constructor Injection) 방식입니다.
<br>
사실 의존성을 주입하는 방법에는 생성자 주입 뿐만 아니라 수정자 주입(Setter Injection), 필드 주입(Field Injection) 방식도 있습니다. 하지만 공식 문서에서는 특히 생성자 주입 방식을 권장합니다.
- 우선 생성 시점에 딱 한 번만 호출되므로 주입받은 의존성이 런타임에 변하지 않음(**불변성**)을 보장합니다.
- 또한 필드에 final 키워드를 사용할 수 있어, 만약 의존성 주입을 깜빡하더라도 애플리케이션 실행 전 컴파일 단계에서 바로 에러를 잡아낼 수 있습니다. 즉, **필수 의존성**을 보장하는거죠.
- 그 외에도 순환 참조를 방지할 수 있고, 테스트 시에 Mock 객체를 사용하기에 용이하다는 등의 이점이 있습니다.

그럼 이제 제어가 역전된다는게 어떤 느낌인지 알겠죠?
<br>
만약 아직도 이해가 안된다면 [의존성 주입 3분만에 이해하기 (Dependency Injection, Inversion of Control)](https://youtu.be/1vdeIL2iCcM?si=_hvHcxvKs-Z8tq3b) 영상을 추천드립니다.
<br>
이해가 되셨다면! 실제 스프링에서는 아래와 같이 제어의 역전이 사용됩니다. 

```java
import org.springframework.stereotype.Service;

@Service
public class UserService {
    // 구체적인 구현체가 아닌 인터페이스에 의존
    private final UserRepository userRepository;

    // 스프링 컨테이너가 UserService를 생성할 때 UserRepository 구현체를 자동으로 주입 (DI)
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    public void registerUser() {
        userRepository.save();
    }
}

```

### ② AOP

자, 이제 AOP입니다. 여러분은 모두 AOP를 잘 사용하고 있을겁니다. 자주 쓰이는 `@Transactional` 또한 AOP의 일종이라고 할 수 있기 때문이죠.
이런 AOP는 Aspect-Oriented Programming의 줄임말데요, 말 그대로 관점 지향 프로그래밍을 뜻합니다.
AOP, 관점 지향 프로그래밍이라는 말만 들으면 OOP, 객체 지향 프로그래밍과 공존할 수 없는 것처럼 들릴 수 있습니다. 하지만 AOP는 OOP 프로그래밍을 보완하는 구조입니다.
실제로 [Spring의 공식 문서](https://docs.spring.io/spring-framework/reference/core/aop.html#page-title)에는 다음과 같이 쓰여있습니다.

> Aspect-oriented Programming (AOP) complements Object-oriented Programming (OOP) by providing another way of thinking about program structure.

그리고 관점 지향은 쉽게 말해, '핵심 비즈니스 로직'과 '반복되는 부가 기능'을 완벽하게 분리해 내는 기술입니다. 즉, 애플리케이션 전반에 흩어져 있는 교차 관심사(ex. 로깅, 보안, 트랜잭션)를 별도의 모듈(Aspect)로 분리합니다.
<br><br>
예를 들어 은행 시스템을 개발한다고 상상해 볼까요?
<br><br>
<img src="img/spring_aop.png" width="70%">
<br><br>
'계좌 이체', '대출 승인', '잔액 조회'처럼 각 모듈이 수행해야 하는 진짜 비즈니스 로직이 있습니다. 반면, '보안 검사', 'DB 연동(트랜잭션)', '실행 시간 로깅' 같은 인프라 로직은 이체, 대출, 조회 기능을 실행할 때마다 공통으로 앞뒤에 들어가야 하죠.
<br>
이런 여러 고유 기능들(세로)을 가로지르며(횡단하며) 똑같이 나타나는 로직들을 '횡단 관심사(Cross-cutting Concerns)'라고 부릅니다.
AOP는 이렇게 가로로 겹치는 횡단 관심사들을 가위로 오려내어 별도의 클래스(Aspect)에 따로 모아둡니다. 그리고 프레임워크에 이 코드들을 런타임 시 앞뒤로 끼워 넣어달라고 설정해둡니다.
결과적으로 개발자는 복잡한 인프라 코드를 신경 쓸 필요 없이, '계좌 이체'라는 순수한 핵심 로직 작성에만 100% 집중할 수 있게 되는거죠.
<br>
만약 AOP가 없다면 개발자는 계좌 이체 코드에도, 대출 승인 코드에도 매번 보안검사(), 트랜잭션시작(), 로깅() 코드를 똑같이 복사해서 붙여넣어야 할겁니다. 보안 검사 방식이 업데이트라도 되면 수백 개의 파일을 열어 일일이 수정해야 하는 끔찍한 일이 벌어지고, 비즈니스 로직은 단 3줄인데, 부가 기능 코드가 20줄을 차지해 코드를 읽기도 힘들어지게 됩니다.
<br><br>
그러면 AOP는 어떻게 적용할 수 있을까요? AOP를 실제 코드에 적용하는 방식은 시점에 따라 크게 컴파일 타임, 클래스 로드 타임, 런타임 세 가지로 나뉩니다. 그리고 Spring AOP는 이 중 프록시 패턴을 기반으로 한 런타임(Run-time) 방식을 사용합니다.
스프링 컨테이너가 IoC/DI를 통해 객체(Bean)의 생성과 의존성 주입을 직접 통제하기 때문에, 실제 객체 대신 부가 기능이 씌워진 프록시 객체를 런타임에 동적으로 주입할 수 있는거죠.
<br>
아래와 같은 예시 코드가 AOP를 잘 보여줍니다.

```java
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    // Service 클래스의 모든 메서드 실행 전후에 공통 로깅 기능 적용
    @Around("execution(* com.example..*Service.*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        
        Object proceed = joinPoint.proceed(); // 실제 핵심 비즈니스 로직 실행
        
        long executionTime = System.currentTimeMillis() - start;
        System.out.println(joinPoint.getSignature() + " 실행 시간: " + executionTime + "ms");
        return proceed;
    }
}
```

더 나은 AOP의 이해를 위해 [[10분 테코톡] 🌕제이의 Spring AOP](https://youtu.be/Hm0w_9ngDpM?si=hRbn6y1DWXYCic4F) 영상을 추천드립니다.

### ③ PSA

마지막으로 PSA입니다. PSA는 Portable Service Abstraction의 줄임말로, 일관된 서비스 추상화라고도 합니다.

쉽게 말하면 PSA는 어떤 기술을 사용하든 개발자가 비슷한 방식으로 사용할 수 있도록 중간에 추상화 계층을 만들어주는 것입니다.

예를 들어 우리가 해외여행을 간다고 생각해봅시다.
<br>
한국에서는 콘센트에 충전기를 바로 꽂을 수 있지만, 다른 나라에서는 콘센트 모양이나 전압이 다를 수 있습니다. 그렇다고 여행을 갈 때마다 노트북이나 휴대폰의 충전 방식을 전부 고칠 수는 없겠죠.
그래서 우리는 어댑터를 사용합니다. 사용자는 똑같은 충전기를 사용하고, 어댑터가 각 나라의 콘센트 규격에 맞게 변환해주죠.

Spring의 PSA도 비슷합니다.
개발자가 특정 기술의 세부적인 사용 방법을 일일이 알지 못하더라도, Spring이 제공하는 일관된 인터페이스를 사용하면 내부 구현 기술을 비교적 자유롭게 변경할 수 있도록 만들어줍니다.

대표적인 예가 바로 트랜잭션 추상화입니다.
앞의 AOP 설명에서 나왔던 `@Transactional`을 다시 생각해볼까요?

```java
@Service
public class OrderService {
    
    @Transactional
    public void createOrder() {
        // 주문 저장
        // 결제 처리
        // 재고 감소
    }
}
```

개발자인 우리는 그냥 `@Transactional`을 붙입니다.
<br>
그런데 실제로 트랜잭션을 처리하는 기술은 프로젝트마다 다를 수 있습니다.
어떤 프로젝트에서는 JDBC를 사용할 수도 있고, 다른 프로젝트에서는 JPA를 사용할 수도 있고, 또 다른 환경에서는 JTA와 같은 트랜잭션 기술을 사용할 수도 있습니다.
만약 Spring의 추상화가 존재하지 않는다면 사용하는 기술이 바뀔 때마다 개발자가 트랜잭션 시작, 커밋, 롤백을 처리하는 코드를 해당 기술의 API에 맞게 다시 작성해야 할 수도 있습니다.
하지만 Spring은 이들 사이에 트랜잭션 추상화 계층을 제공합니다.
그 중심에 있는 인터페이스가 `PlatformTransactionManager`입니다.

```java
public interface PlatformTransactionManager extends TransactionManager {
    
    TransactionStatus getTransaction(TransactionDefinition definition)
            throws TransactionException;
    
    void commit(TransactionStatus status)
            throws TransactionException;
    
    void rollback(TransactionStatus status)
            throws TransactionException;
}
```

실제로 사용하는 기술에 따라서 이 인터페이스의 구현체가 달라집니다.
예를 들어 JPA를 사용한다면 `JpaTransactionManager`가 사용될 수 있고, JDBC 기반 환경이라면 JDBC에 맞는 TransactionManager가 사용될 수 있습니다.
중요한 것은 비즈니스 로직을 작성하는 개발자가 이러한 차이를 직접 처리하지 않아도 된다는거죠.
Spring의 공식 문서에서도 Spring의 트랜잭션 지원이 JDBC, JTA, Hibernate, JPA처럼 서로 다른 트랜잭션 API에 대해 일관된 프로그래밍 모델을 제공한다고 설명합니다.

즉, 우리의 비즈니스 코드는 그대로 두고, 내부에서 사용하는 트랜잭션 기술이나 구현체를 변경할 수 있게 되는 것입니다.
덕분에 애플리케이션의 상위 계층이 특정 DB 기술의 예외 클래스에 강하게 의존하는 것을 줄일 수 있습니다.

결국 한줄로 정리하면, PSA의 핵심은 "구체적인 기술은 바뀌어도, 그것을 사용하는 애플리케이션의 코드는 최대한 바뀌지 않도록 만드는 것"이라고 할 수 있을 것 같습니다.

### ✔ 그래서, IoC/DI, AOP, PSA는 서로 무슨 관계인데?

지금까지 세 가지를 따로 공부했지만, 사실 이 기술들은 서로 독립적으로 존재하는 것이 아닙니다.
<br>
처음에 Spring의 세 가지 핵심 기술이 서로 매우 긴밀하게 협력한다고 했던 것을 기억하시나요?
<br>
`@Transactional` 하나만 살펴봐도 세 기술이 어떻게 협력하는지 알 수 있습니다.

```java
@Service
public class OrderService {
    
    private final OrderRepository orderRepository;
    
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }
    
    @Transactional
    public void createOrder() {
        orderRepository.save();
    }
}
```

먼저 **IoC/DI**를 통해 Spring 컨테이너가 `OrderService`, `OrderRepository`, `TransactionManager` 등의 객체를 생성하고 서로 연결해줍니다.
그리고 `@Transactional`이 붙은 메서드에는 **AOP**가 적용됩니다.
Spring은 실제 `OrderService` 앞에 프록시 객체를 두고 메서드 실행 전 트랜잭션을 시작하고, 정상적으로 끝나면 커밋하고, 문제가 생기면 롤백합니다.
Spring의 선언적 트랜잭션 역시 이러한 프록시와 인터셉터 구조를 사용합니다.
마지막으로 실제 트랜잭션 처리는 PSA를 통해 추상화되어 있습니다.
개발자는 JDBC인지 JPA인지 JTA인지에 따라 서로 다른 코드를 작성하는 대신 Spring의 일관된 트랜잭션 추상화를 사용합니다.

결국 세 가지를 합쳐보면 다음과 같습니다.

```bash
                    Spring Container
                          │
                      IoC / DI
                          │
                          ▼
                  ┌────────────────┐
                  │ OrderService   │
                  │                │
                  │ createOrder()  │← 핵심 비즈니스 로직
                  └────────────────┘
                          ▲
                          │
                      AOP Proxy
                 ┌────────┴────────┐
                 │ Transaction     │
                 │ Logging         │
                 │ Security ...    │
                 └─────────────────┘
                          │
                         PSA
             ┌────────────┼────────────┐
             ▼            ▼            ▼
           JDBC          JPA          JTA
```

- IoC/DI는 객체 간의 관계를 유연하게 만들고,
- AOP는 반복되는 부가 기능을 핵심 로직으로부터 분리하며,
- PSA는 구체적인 기술의 차이를 추상화합니다.

그리고 그 결과 개발자는 특정 프레임워크나 인프라 기술에 지나치게 얽매이지 않은 POJO 중심의 비즈니스 로직을 작성할 수 있게 됩니다.

이제 Spring 코드를 보면

> "IoC/DI로 관리되는 Bean에 AOP 프록시가 적용되고, 그 뒤에서는 Spring의 서비스 추상화를 통해 실제 기술이 동작하고 있구나!"

라고 생각할 수 있지 않을까요? 😽

---

## 2️⃣ Spring Bean 이 무엇이고, Bean 의 라이프사이클과 Bean Scope에 대해 조사해요

Spring을 사용하다 보면 Bean이라는 단어를 자주 듣게 됩니다.

```java
@Service
public class UserService {
  ...
}
```
이런 코드를 작성할 때도, "`UserService`는 Spring Bean으로 등록된다"라고 하는데요,
우린 그저 평범한 Java 클래스에 `@Service`라는 어노테이션을 붙인 것 뿐인데 대체 Spring은 이 클래스를 어떻게 발견하는거고,
Bean으로 등록된다는건 정확히 무슨 뜻인걸까요?

Spring Bean이 애플리케이션 시작 과정에서 어떻게 발견되고, 만들어지고, 사용되다가, 사라지는지 하나씩 알아봅시다!

### Spring Bean?

먼저 Bean부터 알아봅시다.
Spring Bean이란, Spring IoC Container가 생성하고 관리하는 객체입니다.

> A bean is an object that is instantiated, assembled, and managed by a Spring IoC container.
> <br>
> [Spring 공식문서](https://docs.spring.io/spring-framework/reference/core/beans/introduction.html)

예를 들어 아래와 같은 평범한 객체가 있다고 해볼까요?

```java
public class Chef {

    public void cook() {
        System.out.println("국수 완성!");
    }
}
```

우리가 직접 객체를 생성하면,

```java
Chef chef = new Chef();
```

이 `Chef` 객체의 생성과 관리에 대한 책임은 전적으로 개발자에게 있습니다.

반면,

```java
@Component
public class Chef {

    public void cook() {
        System.out.println("국수 완성!");
    }
}
```

Spring이 `Chef`를 발견하고 객체를 생성해서 Spring Container 안에서 관리하기 시작하면, 이 객체를 **Spring Bean**이라고 부릅니다.

Spring 공식 문서에서도 Bean을 구성하기 위한 정보를 **Bean Definition**으로 관리하며, 실제 애플리케이션 객체들은 이러한 Bean Definition을 바탕으로 Spring Container가 생성하고 관리한다고 설명합니다.

그런데 여기서 중요한 개념 하나가 등장합니다.

바로 **BeanDefinition**입니다.
<br>
BeanDefinition은 쉽게 말하면 Bean을 만들기 위한 설계도라고 볼 수 있습니다.
예를 들어 Spring은, 먼저

_"UserService라는 클래스를
singleton으로 만들고,
이런 의존성을 주입해서,
userService라는 이름으로 관리해야겠다."_

라는 정보를 등록합니다.
<br>
이 정보가 바로 `BeanDefinition`입니다.

그리고 이후 실제 Bean을 생성할 시점이 되면 Spring Container가 이 설계도를 보고 객체를 생성합니다.

```text
@Component 발견
      ↓
BeanDefinition 생성
      ↓
BeanDefinitionRegistry에 등록
      ↓
필요한 시점에 실제 객체 생성
      ↓
Spring Bean
```

### Annotation

그런데, 어노테이션은 대체 뭘까요?
<br>
Spring을 사용하면 정말 많은 어노테이션을 사용합니다.

`@Component`, `@Service`, `@Repository`, `@Controller`, `@Autowired`, `@Transactional`, `@Configuration`, `@Bean`, ...

Java에서 Annotation은 "코드에 추가적인 정보를 붙여두는 메타데이터"입니다.

예를 들어 우리가 직접 어노테이션을 만들 수도 있습니다.
```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MyAnnotation {

    String value() default "";
}
```

그리고 아래와 같이 사용할 수 있습니다.

```java
@MyAnnotation("hello")
public class UserService {
}
```

하지만 여기서 어노테이션 자체는 아무런 행동도 하지 않는데요.
`@MyAnnotation("hello")`를 붙였다고 갑자기 어떤 코드가 실행되는 것이 아니라, 누군가 이 어노테이션을 읽고, 의미를 부여해야 하는거죠.

Java의 클래스는 `getAnnotation()`, `isAnnotationPresent()` 같은 API를 제공하므로 런타임에 특정 어노테이션이 존재하는지 확인할 수 있습니다.

그럼 우리도 아래와 같이 어노테이션을 직접 읽어볼 수도 있겠죠?

```java
MyAnnotation annotation =
        UserService.class.getAnnotation(MyAnnotation.class);

System.out.println(annotation.value());
```

결과는 다음과 같습니다.

```text
hello
```

즉, Annotation은 메타데이터이고, Annotation에 그것을 해석하는 프로그램이 함께하면 실제 기능이 된다고 이해할 수 있을 것 같습니다.

Spring도 마찬가지인데요, UserService에 붙은 `@Service`가 단순히 스스로 객체를 만드는 것이 아닙니다.
Spring이 `@Service`라는 메타데이터를 발견하고, 이 클래스는 내가 관리해야겠다고 판단하기 때문에 Bean으로 등록되는 것입니다.

그럼 Java에서 어노테이션은 어떻게 구현하는지 조금 더 살펴볼까요?

Java의 어노테이션은 `@interface`라는 문법으로 정의합니다.

```java
public @interface MyAnnotation {}
```

이 문법은 단순히 `interface` 앞에 골뱅이를 붙인 이상한 문법처럼 보이지만, Java Language Specification에서는 Annotation Interface를 일반 Interface와 구분되는 특별한 형태의 Interface로 정의하고 있습니다.
또한 Annotation Interface는 직접적으로 `java.lang.annotation.Annotation`을 상위 인터페이스로 가집니다.

어노테이션을 만들 때 자주 등장하는 것이 바로 메타 어노테이션(Meta Annotation)인데요,
<br>
대표적으로 다음과 같은 것들이 있습니다.

| 어노테이션         | 역할                                   |
| ------------- | ------------------------------------ |
| `@Target`     | 이 어노테이션을 어디에 붙일 수 있는지 지정             |
| `@Retention`  | 어노테이션 정보를 언제까지 유지할지 지정               |
| `@Documented` | JavaDoc 등에 어노테이션 정보를 포함              |
| `@Inherited`  | 자식 클래스가 부모 클래스의 어노테이션을 상속받을 수 있도록 설정 |

예를 들어,

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MyAnnotation {
}
```

이라고 정의했다면,

`@Target(ElementType.TYPE)`은 클래스나 인터페이스 같은 **타입에 붙일 수 있다**는 의미이고,
<br>
`@Retention(RetentionPolicy.RUNTIME)`은 **실행 중에도 해당 어노테이션 정보를 확인할 수 있도록 유지한다**는 뜻입니다.

Retention에는 크게 세 가지 정책이 있습니다.

- `SOURCE`
  - 소스 코드에만 존재
  - 컴파일 후 사라짐
- `CLASS`
  - .class 파일까지 존재
  - 일반적으로 Reflection을 통해 런타임에 사용할 수 없음
- `RUNTIME`
  - .class 파일에도 존재
  - 런타임에도 조회 가능

`@Retention`을 지정하지 않으면 기본값은 `CLASS`이며, `RUNTIME`으로 지정한 어노테이션은 Java Reflection API에서도 사용할 수 있습니다.
<br>
Spring의 `@Component` 역시 `RUNTIME`으로 유지되는 어노테이션입니다.

그럼 자주 쓰이는 `@Component`, `@Service`, `@Repository`, `@Controller`는 뭐가 다른걸까요?
<br>
사실 이들의 뿌리는 모두 `@Component`입니다.
<br>
예를 들어, 개념적으로 `@Service`는 다음과 같은 구조를 가지고 있습니다.

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface Service {

    String value() default "";
}
```

즉, `@Service` 위에 다시 `@Component`가 붙어 있습니다.
<br>
이렇게 **어노테이션 위에 붙어 있는 어노테이션**을 Meta Annotation이라고 합니다.

따라서 Spring은

```java
@Component
public class A {
}
```

뿐만 아니라

```java
@Service
public class B {
}
```

도 `@Component` 계열의 Bean 후보로 인식할 수 있습니다.

Spring 공식 문서에서도 `@Service`, `@Repository`, `@Controller`를 `@Component`의 특수화된 stereotype이라고 설명합니다.
또한 `@Component`를 Meta Annotation으로 가지고 있는 사용자 정의 어노테이션 역시 컴포넌트 스캔의 대상이 될 수 있습니다.

### @ComponentScan

그런데 Spring은 `@Service`가 붙은 클래스를 어떻게 찾는 걸까요?

우리는 Spring Boot 애플리케이션을 만들면 보통 다음 코드로 시작합니다.

```java
@SpringBootApplication
public class MyApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }
}
```

그런데 어디에도

`new UserService();`, `new OrderService();`, `new PaymentService();` 같은 코드는 없습니다.

그런데도 Spring은 우리가 만든 수많은 Service, Repository, Controller를 알아서 찾아냅니다.
그 방법 중 하나가 바로 `@ComponentScan`입니다.

`@SpringBootApplication`을 뜯어볼까요?
<br>
`@SpringBootApplication`은 하나의 단순한 어노테이션처럼 보이지만 내부적으로 여러 설정을 합쳐놓은 합성 어노테이션(Composed Annotation)입니다.

핵심적으로 아래와 같은 세 기능을 포함합니다.

```text
@SpringBootApplication
        │
        ├── @SpringBootConfiguration
        ├── @EnableAutoConfiguration
        └── @ComponentScan
```

즉 우리가 직접 `@ComponentScan`을 작성하지 않아도 Spring Boot의 `@SpringBootApplication`이 Component Scan 기능을 활성화 하는거죠.

그럼 `@ComponentScan`은 어디까지 탐색할까요?

다음과 같은 프로젝트가 있다고 해봅시다.

```text
com.example.myapp
│
├── MyApplication.java
│
├── controller
│   └── UserController.java
│
├── service
│   └── UserService.java
│
└── repository
    └── UserRepository.java
```

`MyApplication`이

```java
package com.example.myapp;

@SpringBootApplication
public class MyApplication {
}
```

에 있다면 기본적인 컴포넌트 스캔 범위는 `com.example.myapp`와 그 하위 패키지가 됩니다.
<br>
따라서 `com.example.myapp.controller`, `com.example.myapp.service`, `com.example.myapp.repository`에 있는 컴포넌트들을 발견할 수 있습니다.

Spring의 `@ComponentScan`은 별도의 `basePackages`가 지정되지 않으면 **해당 어노테이션을 선언한 클래스의 패키지부터 탐색**합니다.
Spring Boot 역시 메인 애플리케이션 클래스를 프로젝트의 루트 패키지에 두는 것을 권장합니다.

반대로 이런 구조라면 어떻게 될까요?

```text
com.example.app
└── MyApplication.java

com.example.service
└── UserService.java
```

`UserService`는 `MyApplication`의 하위 패키지가 아니기 때문에 기본 Component Scan으로 발견되지 않을 수 있습니다.
<br>
이런 경우에는 직접 범위를 지정할 수도 있습니다.

```java
@ComponentScan(basePackages = "com.example")
@Configuration
public class AppConfig {
}
```

하지만 일반적인 Spring Boot 프로젝트에서는 메인 클래스를 최상위 패키지에 두는 것만으로 대부분의 문제를 피할 수 있습니다.

이제 좀 더 깊이 들어가 볼까요?
`@ComponentScan` 내부에서는 무슨 일이 일어날까요?

다음과 같은 코드가 있다고 해보겠습니다.

```java
@Configuration
@ComponentScan("com.example")
public class AppConfig {
}
```

그리고

```java
@Service
public class UserService {
}
```

가 존재한다고 해볼까요?

전체적인 과정은 다음과 같습니다.

```text
Spring Container 시작
        ↓
@Configuration 클래스 분석
        ↓
@ComponentScan 발견
        ↓
지정된 package 탐색
        ↓
.class 파일 검색
        ↓
Annotation metadata 분석
        ↓
@Component 계열인지 확인
        ↓
BeanDefinition 생성
        ↓
BeanDefinitionRegistry에 등록
        ↓
실제 Bean 생성
```

이렇게 보니 너무너무 길어보이고, 정확히 어떤 뜻인지 모르겠으니, 하나씩 조금 더 자세히 살펴봅시다.

#### ① Spring Container의 설정 클래스 분석

Spring이 ApplicationContext를 초기화하면서 설정 정보를 읽습니다.
이 과정에서 `@Configuration` 클래스 등을 처리하는 핵심 컴포넌트 중 하나가 `ConfigurationClassPostProcessor`입니다.
<br>
이 객체는 `@Configuration` 클래스를 처리하고, 그 내부의 `@ComponentScan`, `@Bean`, `@Import` 등의 설정을 분석하여 추가적인 BeanDefinition을 등록합니다.
`ConfigurationClassPostProcessor`는 Spring에서 Configuration 클래스를 부트스트랩하기 위한 `BeanFactoryPostProcessor`입니다.

#### ② `@ComponentScan` 발견

설정 클래스를 분석하다가 다음을 발견합니다.

```java
@ComponentScan("com.example")
```

그러면 Spring은 _"com.example 아래에서 Component 후보를 찾아야겠다"_ 라고 판단합니다.
이 과정에서 사용되는 핵심 클래스가 `ClassPathBeanDefinitionScanner`입니다.

#### ③ Classpath 탐색

Scanner는 지정한 패키지를 Classpath상의 경로로 변환합니다.

개념적으로, `com.example`이 `com/example` 형태로 변환되고, 그 아래의 `.class` 파일들을 탐색합니다.

```text
com/example/UserService.class
com/example/OrderService.class
com/example/UserRepository.class
...
```

이때 Spring이 모든 클래스를 무조건 `Class.forName(...)`으로 로딩한 뒤 Reflection을 돌리는 것은 아닙니다.

일반적인 classpath scanning 과정에서 Spring의 `ClassPathScanningCandidateComponentProvider`는 `MetadataReader`를 사용하며, 이 메타데이터 읽기 기능은 ASM의 `ClassReader`를 기반으로 동작합니다.

쉽게 말하면, 클래스를 실제로 모두 객체화하거나 로딩해서 검사하기보다 `.class` 파일의 메타데이터를 읽어 어노테이션 등의 정보를 확인할 수 있다는 것입니다.

그래서

```text
UserService.class
        ↓
ASM으로 클래스 메타데이터 읽기
        ↓
@Service 발견
        ↓
@Service 위의 @Component 확인
        ↓
Bean 후보!
```

와 같은 과정이 가능합니다.

#### ④ Bean 후보인지 필터링

`ClassPathBeanDefinitionScanner`는 모든 클래스를 Bean으로 등록하지 않습니다.

기본적으로 `@Component` 또는 `@Component`를 Meta Annotation으로 가지고 있는 클래스들을 후보로 판단합니다.

따라서,

```java
public class NormalClass {
}
```

는 기본 스캔 대상이 아니지만

```java
@Component
public class ComponentClass {
}
```

나

```java
@Service
public class UserService {
}
```

는 후보가 됩니다.

`@ComponentScan`에는 필터도 지정할 수 있습니다.

```java
@ComponentScan(
    basePackages = "com.example",
    excludeFilters = {
        @ComponentScan.Filter(
            type = FilterType.ANNOTATION,
            classes = Controller.class
        )
    }
)
```

이런 식으로 특정 Component를 제외하거나 사용자 정의 조건에 맞는 클래스만 포함시킬 수도 있습니다.

#### ⑤ BeanDefinition 만들기

Bean 후보를 찾았다고 바로 `new UserService();`를 실행하는 것은 아닙니다.

먼저 `BeanDefinition`을 만듭니다.

개념적으로는 다음과 같은 정보가 담긴다고 생각하면 됩니다.

```text
BeanDefinition
──────────────────────────
Bean Class   : UserService
Bean Name    : userService
Scope        : singleton
Lazy         : false
Primary      : false
Dependencies : ...
──────────────────────────
```

즉, "나중에 UserService 객체를 만들 때 이렇게 만들어라."라는 설계도라고 할 수 있겠네요.

#### ⑥Bean 이름 결정 및 등록

Bean은 Container 안에서 이름도 가집니다.

예를 들어

```java
@Service
public class UserService {
}
```

는 기본적인 이름 생성 규칙에 따라 보통 `userService`라는 이름으로 등록됩니다.

직접 이름을 지정할 수도 있습니다.

```java
@Service("customUserService")
public class UserService {
}
```

이 경우 Bean의 이름은 `customUserService`가 됩니다.

그리고 최종적으로 BeanDefinition이 `BeanDefinitionRegistry`에 등록됩니다.

여기까지가 크게 보면 **Bean 등록 과정**입니다.

그럼 Bean은 언제 **실제 객체**가 되는걸까요?

BeanDefinition이 모두 준비되었다면 이제 Spring Container는 실제 Bean을 생성합니다.
특히 기본 Scope인 `singleton` Bean은 일반적으로 ApplicationContext가 초기화되는 과정에서 미리 생성됩니다.
물론 `@Lazy`가 붙어 있다면 실제로 필요할 때까지 생성을 미룰 수도 있습니다.

이제 드디어 **Bean Lifecycle**이 시작됩니다.

### Bean Lifecycle

Spring Bean도 태어나고, 살아가고, 죽습니다.

전체적인 라이프사이클을 단순화하면 다음과 같습니다.

> 인스턴스화 → 의존성 주입(DI) → 초기화 콜백(Initialization) → 빈 사용 → 소멸 콜백(Destruction)

조금 더 자세하게 풀면 아래와 같다고 볼 수 있죠.

```text
1. BeanDefinition 등록
        ↓
2. 의존성 확인
        ↓
3. Bean 객체 생성
        ↓
4. 의존성 주입
        ↓
5. 초기화 전 BeanPostProcessor
        ↓
6. @PostConstruct 등 초기화
        ↓
7. 초기화 후 BeanPostProcessor
        ↓
8. Bean 사용
        ↓
9. Container 종료
        ↓
10. @PreDestroy 등 소멸 처리
```

여기서 **BeanPostProcessor**는 무엇일까요?

`BeanPostProcessor`는 이름 그대로 "Bean을 생성하는 과정에 끼어들어 추가적인 처리를 할 수 있도록 해주는 확장 지점"입니다.

대표적으로 다음 두 메서드를 제공합니다.

```java
public interface BeanPostProcessor {

    default Object postProcessBeforeInitialization(
            Object bean,
            String beanName) {
        return bean;
    }

    default Object postProcessAfterInitialization(
            Object bean,
            String beanName) {
        return bean;
    }
}
```

즉,

```text
Bean 생성
   ↓
BeanPostProcessor
Before Initialization
   ↓
초기화
   ↓
BeanPostProcessor
After Initialization
   ↓
Bean 사용
```

같은 구조가 됩니다.

Spring 공식 문서에서도 `BeanPostProcessor`가 Bean의 초기화 콜백 전후에 호출되며, Bean을 검사하거나 심지어 프록시 객체로 감싸 반환할 수도 있다고 설명합니다.
실제 Spring AOP 인프라의 일부 역시 이러한 `BeanPostProcessor` 메커니즘을 활용합니다.

어, 근데! 어디서 많이 본 것 같지 않나요?
<br>
앞서 AOP를 공부할 때 실제 객체를 Proxy로 감싼다는 이야기를 했습니다.
<br>
바로 이런 Spring의 확장 지점 덕분에 Bean 생성 과정에서 Proxy 적용 같은 추가 작업을 수행할 수 있는 것입니다.

### Bean Scope

Bean Lifecycle을 이해했다면 이번에는 **Bean이 얼마나 오래 살아있는지** 알아볼까요?
<br>
이걸 결정하는 것이 바로 **Bean Scope**입니다.

Scope는 쉽게 말하면,
<br>
_"Bean 객체를 몇 개 만들고, 어느 범위까지 공유할까?"_
<br>
를 결정합니다.

Spring이 제공하는 대표적인 Scope는 다음과 같습니다.

| Scope         | 의미                                               |
| ------------- | ------------------------------------------------ |
| `singleton`   | 하나의 Spring Container에서 BeanDefinition 하나당 하나의 객체 |
| `prototype`   | Bean을 요청할 때마다 새로운 객체                             |
| `request`     | HTTP Request 하나당 하나                              |
| `session`     | HTTP Session 하나당 하나                              |
| `application` | ServletContext 하나당 하나                            |
| `websocket`   | WebSocket 하나당 하나                                 |

Spring Framework는 이 여섯 종류의 기본 Scope를 제공하며, `request`, `session`, `application`, `websocket`은 Web-aware ApplicationContext에서 사용할 수 있습니다.

몇가지 더 자세히 알아볼까요?

#### ① singleton

Spring Bean의 기본 Scope입니다.
아무것도 지정하지 않으면 기본적으로 singleton이 적용됩니다.

```java
@Service
public class UserService {
}
```

라는 코드는 개념적으로는

```java
@Scope("singleton")
@Service
public class UserService {
}
```

와 비슷합니다.

예를 들어

```java
UserService userService1 = context.getBean(UserService.class);
UserService userService2 = context.getBean(UserService.class);
```

라고 하면 일반적인 singleton Bean에서는 `userService1 == userService2`가 `true`인거죠.
즉, 여러 곳에서 동일한 Bean을 공유하는 것입니다.

여기서 주의해야 할 점은, singleton이라고 해서 Thread-safe한 것이 아니라는 것입니다.

예를 들어

```java
@Service
public class CounterService {

    private int count = 0;

    public void increase() {
        count++;
    }
}
```

같은 mutable state를 singleton Bean 안에 두면 여러 요청이 동시에 같은 객체를 사용하면서 동시성 문제가 발생할 수 있습니다.
<br>
그래서 일반적인 Service Bean은 가능한 한 **상태를 가지지 않는 Stateless 객체**로 설계하는 것이 좋습니다.

#### ② prototype

Prototype Scope는 Bean을 요청할 때마다 새로운 객체를 생성합니다.

```java
@Component
@Scope("prototype")
public class PrototypeBean {
}
```

그리고

```java
PrototypeBean bean1 =
        context.getBean(PrototypeBean.class);

PrototypeBean bean2 =
        context.getBean(PrototypeBean.class);
```

를 실행한다면 `bean1 == bean2`는 `false`가 되겠죠.
<br>
Spring 공식 문서에서도 prototype Bean은 해당 Bean이 요청될 때마다 새로운 인스턴스를 생성한다고 설명합니다.

그런데 prototype에는 아주 중요한 특징이 있습니다.
<br>
Spring은 Prototype Bean의 **생성, 의존성 주입, 초기화**까지는 관리하지만,
Bean을 사용자에게 넘겨준 이후의 **완전한 lifecycle까지 계속 추적하지 않습니다.**
따라서 prototype Bean의 destruction callback은 컨테이너가 자동으로 호출해주지 않습니다. 자원 정리가 필요한 Prototype Bean이라면 이를 직접 처리해야 합니다.

#### ③ request

Web Application에서 사용할 수 있는 Scope입니다.

```java
@Component
@RequestScope
public class RequestInfo {
}
```

HTTP Request 하나마다 별도의 Bean이 만들어집니다.

```text
Request A ──→ RequestInfo A
Request B ──→ RequestInfo B
Request C ──→ RequestInfo C
```

한 Request 내부에서는 같은 객체를 사용하지만 다른 Request와는 공유하지 않습니다.

#### ④ session

HTTP Session 하나마다 Bean을 생성합니다.

```java
@Component
@SessionScope
public class UserSession {
}
```

로그인 사용자별 상태 등을 관리하는 상황을 떠올릴 수 있겠네요.
다만 서버의 세션 상태 관리 방식과 확장성을 고려해야 하므로 무분별하게 사용하는 것은 피하는 것이 좋습니다.

### 정리

그럼 우리 지금까지 배운 내용을 한 번에 이어볼까요?
어노테이션으로 Bean이 등록되는 전체 과정을 다시 연결해봅시다.

```java
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void init() {
        System.out.println("UserService 준비 완료!");
    }
}
```

그리고 애플리케이션을 실행합니다.

```java
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

그러면 개념적으로 아래와 같은 과정이 벌어집니다.

```text
SpringApplication.run()
          ↓
ApplicationContext 생성
          ↓
@SpringBootApplication 분석
          ↓
@ComponentScan 발견
          ↓
base package 결정
          ↓
classpath 탐색
          ↓
UserService.class 발견
          ↓
ASM / MetadataReader로
annotation metadata 확인
          ↓
@Service 발견
          ↓
@Service의 meta annotation인
@Component 확인
          ↓
Bean Candidate 결정
          ↓
UserService용 BeanDefinition 생성
          ↓
BeanDefinitionRegistry 등록
          ↓
singleton Bean 생성 시점
          ↓
UserService 생성자 확인
          ↓
UserRepository Bean 탐색
          ↓
UserRepository 주입
          ↓
UserService 객체 생성
          ↓
BeanPostProcessor 처리
          ↓
@PostConstruct 호출
          ↓
필요한 후처리 및 Proxy 생성
          ↓
Singleton Bean으로 관리
          ↓
애플리케이션에서 사용
          ↓
ApplicationContext 종료
          ↓
@PreDestroy 등의 소멸 처리
```

처음에는 `@Service` 한 줄밖에 안 썼는데 그 뒤에서는 이렇게 많은 일이 벌어지고 있었던 것입니다.

### @Bean

근데, 우리는 `@Bean`이라는 어노테이션도 알고 있습니다.
`@Bean`으로 등록하는 것은 어떻게 다를까요?

Bean을 등록하는 방법이 Component Scan만 있는 것은 아닙니다.
우리는 다음과 같이 직접 Bean을 등록할 수도 있습니다.

```java
@Configuration
public class AppConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

`@Component` 방식은 "Spring아, 이 패키지를 뒤져서 Bean 후보를 찾아줘" 에 가깝다면,
`@Bean` 방식은 "Spring아, 이 메서드가 반환하는 객체를 Bean으로 등록해줘" 에 가깝습니다.

Spring 공식 문서에서도 `@Bean`은 해당 메서드가 생성하고 구성한 객체를 Spring IoC Container가 관리하도록 정의하는 방법이며, `@Configuration`은 Bean Definition의 소스 역할을 하는 클래스라고 설명합니다.

그래서 우리가 직접 만든 클래스에는 보통 `@Service`, `@Repository`, `@Controller`, `@Component` 등을 사용하고,
외부 라이브러리 객체처럼 우리가 해당 클래스 소스에 직접 `@Component`를 붙일 수 없는 경우에는 `@Bean`을 이용해서 등록하는 경우가 많습니다.

### 🤔 하나의 Interface를 구현한 Service가 여러 개라면?

마지막으로 실무에서 자주 만나게 되는 상황을 살펴봅시다.

결제 기능이 있다고 해볼까요?

```java
public interface PaymentService {

    void pay();
}
```

그리고 구현체가 두 개 있습니다.

```java
@Service
public class KakaoPaymentService
        implements PaymentService {

    @Override
    public void pay() {
        System.out.println("카카오페이 결제");
    }
}
```

```java
@Service
public class NaverPaymentService
        implements PaymentService {

    @Override
    public void pay() {
        System.out.println("네이버페이 결제");
    }
}
```

이제 다음 코드를 작성하면 어떻게 될까요?

```java
@Service
public class OrderService {

    private final PaymentService paymentService;

    public OrderService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
}
```

Spring 입장에서는 하나의 타입에 여러 Bean이 후보가 되기 때문에 PaymentService를 달라고 요청 받았을 때, 뭘 넣어줘야 하는지 난감해지겠죠.

Spring 공식 문서에서도 type 기반 Autowiring에서 후보가 여러 개 존재할 경우 추가적인 선택 기준이 필요하다고 설명합니다.

#### ① `@Primary`

이 방법은 기본으로 사용할 구현체를 하나 지정합니다.

```java
@Primary
@Service
public class KakaoPaymentService
        implements PaymentService {
}
```

이제

```java
public OrderService(PaymentService paymentService) {
    this.paymentService = paymentService;
}
```

라고 하면 여러 후보 중 `@Primary`가 붙은 `KakaoPaymentService`가 우선적으로 선택됩니다.

#### ② `@Qualifier`

특정 구현체를 명확하게 선택하고 싶다면 `@Qualifier`를 사용할 수 있습니다.

```java
@Service
public class OrderService {

    private final PaymentService paymentService;

    public OrderService(
        @Qualifier("kakaoPaymentService")
        PaymentService paymentService
    ) {
        this.paymentService = paymentService;
    }
}
```

Spring은 `PaymentService` 타입 후보 중 qualifier 조건에 맞는 Bean을 좁혀 선택합니다.

조금 더 의미 있는 이름을 직접 지정할 수도 있습니다.

```java
@Service
@Qualifier("kakao")
public class KakaoPaymentService
        implements PaymentService {
}
```

```java
@Service
public class OrderService {

    private final PaymentService paymentService;

    public OrderService(
        @Qualifier("kakao")
        PaymentService paymentService
    ) {
        this.paymentService = paymentService;
    }
}
```

#### ③ 구현체를 전부 주입받기

Spring은 같은 Interface를 구현한 Bean들을 한 번에 주입할 수도 있습니다.

```java
@Service
public class PaymentManager {

    private final List<PaymentService> paymentServices;

    public PaymentManager(
        List<PaymentService> paymentServices
    ) {
        this.paymentServices = paymentServices;
    }
}
```

이렇게 하면 `PaymentService`를 구현한 Bean들이 Collection으로 들어옵니다.

또는 `Map`으로 받을 수도 있습니다.

```java
@Service
public class PaymentManager {

    private final Map<String, PaymentService> paymentServices;

    public PaymentManager(
        Map<String, PaymentService> paymentServices
    ) {
        this.paymentServices = paymentServices;
    }

    public void pay(String type) {
        paymentServices.get(type).pay();
    }
}
```

### 정리하면,

- **Spring Bean**은 Spring IoC Container가 생성하고 관리하는 객체입니다.
- **Annotation**은 그 자체가 어떤 기능을 실행하는 명령어가 아니라, 프로그램이 읽을 수 있는 Metadata입니다.
- `@ComponentScan`은 Classpath를 탐색하여 `@Component` 또는 이를 Meta Annotation으로 가지고 있는 클래스들을 찾아 BeanDefinition으로 등록합니다.
- **Bean Lifecycle**은 단순화하면, *객체 생성 → 의존성 주입 → 초기화 → BeanPostProcessor → 사용 → 소멸*의 흐름을 가집니다.
- **Bean Scope**는 해당 Bean 객체를 어느 범위까지 공유할 것인지를 결정합니다.

결국 Spring Bean을 제대로 이해한다는 것은
"Spring Container가 어떤 정보를 바탕으로 객체를 발견하고, 생성하고, 연결하고, 관리하는가?"
를 이해하는 것이라고 볼 수 있겠네요!

---

## 3️⃣ 🔥Spring MVC를 심층 분석해요🔥

### 1. MVC 패턴 vs. Spring MVC

- **MVC 패턴 (Model-View-Controller):**<br>
  소프트웨어 공학에서 애플리케이션을 세 가지 역할로 나누는 **설계 아키텍처**로, 데이터(Model), 사용자 인터페이스(View), 그리고 이 둘을 연결하며 제어하는 비즈니스 로직(Controller)을 분리하여 유지보수성을 높이는 것이 목적
- **Spring MVC (`spring-webmvc`):**<br>
  스프링 프레임워크가 제공하는 웹 모듈로, MVC 아키텍처 패턴을 Java의 Servlet API를 기반으로 구현한 **실제 프레임워크**. 일반적인 MVC 구조에 중앙 집중식 요청 처리기인 **프론트 컨트롤러(Front Controller)** 패턴을 도입하여 웹 요청 처리를 표준화하고 유연한 확장을 제공.

### 2. Servlet과 웹 요청 처리

* **Servlet (서블릿):** Java를 사용하여 웹 서버의 기능을 확장하고 동적인 HTTP 응답을 생성하기 위한 자바 표준 인터페이스(규약)<br>
  (Spring MVC 자체가 이 Servlet API를 기반으로 구축되어 있음)
* **웹 요청 처리 흐름:**
  1. 클라이언트(브라우저)가 HTTP 요청을 서버로 전송
  2. 서블릿 컨테이너가 요청을 받아 `HttpServletRequest`와 `HttpServletResponse` 객체를 생성
  3. 요청 URL에 매핑된 서블릿의 `service()` 메서드를 호출. (HTTP 메서드에 따라 내부적으로 `doGet`, `doPost` 등으로 분기)
  4. 로직 처리가 끝나면 `HttpServletResponse`를 통해 클라이언트에게 응답을 보내고, 생성되었던 Request/Response 객체를 소멸시킴

### 3. 톰캣(Tomcat)과 WAS

* **WAS (Web Application Server):** 정적 콘텐츠(HTML, 이미지)만 제공하는 웹 서버와 달리, DB 조회나 복잡한 비즈니스 로직을 통해 **동적인 콘텐츠**를 생성하고 제공하는 미들웨어. 내부적으로 서블릿을 실행할 수 있는 '서블릿 컨테이너'를 포함.
* **Tomcat:** 아파치 소프트웨어 재단에서 개발한 가장 대표적인 오픈소스 서블릿 컨테이너이자 WAS. 스프링 부트(Spring Boot)는 이 톰캣을 애플리케이션 내부에 내장(Embedded Tomcat)하여 제공하므로, 개발자가 별도의 서버 설정 없이 `main()` 메서드 실행만으로 웹 서버를 띄울 수 있음.

### 4. DispatcherServlet과 웹 요청 동작 흐름

스프링 공식 문서는 Spring MVC의 핵심을 `DispatcherServlet`으로 정의함. 이는 모든 HTTP 요청을 가장 먼저 받아 적절한 컴포넌트로 요청을 위임하는 **프론트 컨트롤러(Front Controller)** 역할을 수행.

**동작 흐름:**

1. 요청 수신
2. 핸들러 조회 (`HandlerMapping`)
3. 어댑터 조회 및 실행 (`HandlerAdapter`)
4. 결과 처리 및 응답 반환

---

이번 과제를 수행하면서 관련된 개념들에 대한 유튜브 영상들로부터 많은 도움을 받았습니다. 제가 집중력이 짧아 영상의 도움을 받은 것도 있지만... 생각보다 유튜브에 고수들이 너무나 쉽게 풀어서 설명한 영상들이 참 많답니다? 모두 알고 계시겠지만 한번 상기시켜드리고 갑니다...👍