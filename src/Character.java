public class Character {
        private String name;
        private int hp, attack;

        public Character(String name, int hp, int attack) {
            this.name = name;
            this.hp = hp;
            this.attack = attack;
        }

        public String getName() { return name; }
        public int getHp() { return hp; }
        public int getAttack() { return attack; }

        public void takeDamage(int damage) { hp -= damage; if(hp < 0) hp = 0; }
        public boolean isAlive() { return hp > 0; }
    }