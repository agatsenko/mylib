/**
 * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
 * Created: 2019-09-30
 */
package io.agatsenko.mylib.infrastructure;

public class JavaTester {
    static final Dog dog = new Dog("bobik");
    static final Bird bird = new Bird("super bird");

    public static void main(String[] args) {
        Func<? super Bird, ? extends Animal> feed;
        feed = JavaTester::feedDog;
        final var pet = feed.apply(bird);
        System.out.println("pet name: " + pet.getName());
    }

    @FunctionalInterface
    interface Func<A, R> {
        R apply(A a);
    }

    static Animal feedDog(Animal food) {
        dog.eat(food);
        return dog;
    }

    interface Entity {
    }

    interface Food extends Entity {
    }

    static class Herb implements Entity, FoodBird {
    }

    static abstract class Animal implements Entity {
        void eat(Entity entity) {
            System.out.println("the " + getClass().getSimpleName() + " eats the " + entity.getClass().getSimpleName());
        }

        abstract String getName();

        @Override
        public String toString() {
            return getName();
        }
    }

    static class Bird extends Animal implements FoodDog {
        private final String name;

        Bird(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    interface FoodBird extends Food {
    }

    static class Dog extends Animal {
        private final String name;

        Dog(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    interface FoodDog extends Food {
    }
}
