package main

import (
	"fmt"
	"math/rand"
	"sync"
)

func main() {
	// Ініціалізація даних
	numMonks := 16
	energyLevels := make([]int, numMonks)
	for i := 0; i < numMonks; i++ {
		energyLevels[i] = rand.Intn(100)
	}

	// Виводимо енергії ченців
	fmt.Println("Енергії ченців:", energyLevels)

	// Створюємо канал для результатів
	resultChan := make(chan int, numMonks/2)

	// Запускаємо процес змагань
	var wg sync.WaitGroup
	for i := 0; i < numMonks; i += 2 {
		wg.Add(1)
		go fight(energyLevels[i], energyLevels[i+1], resultChan, &wg)
	}

	// Зачекаємо, поки всі ченці завершать бій
	wg.Wait()
	close(resultChan)

	// Зберігаємо результати в новий масив
	var winners []int
	for res := range resultChan {
		winners = append(winners, res)
	}

	// Виводимо переможців першого раунду
	fmt.Println("Переможці першого раунду:", winners)

	// Продовжуємо бої, поки не залишиться один переможець
	for len(winners) > 1 {
		resultChan = make(chan int, len(winners)/2)
		for i := 0; i < len(winners); i += 2 {
			wg.Add(1)
			go fight(winners[i], winners[i+1], resultChan, &wg)
		}
		wg.Wait()
		close(resultChan)

		winners = winners[:0]
		for res := range resultChan {
			winners = append(winners, res)
		}
		fmt.Println("Переможці цього раунду:", winners)
	}

	// Виводимо фінального переможця
	fmt.Printf("Фінальний переможець: ченець з енергією %d\n", winners[0])
}

func fight(energy1, energy2 int, resultChan chan int, wg *sync.WaitGroup) {
	defer wg.Done()
	if energy1 > energy2 {
		resultChan <- energy1
	} else {
		resultChan <- energy2
	}
	fmt.Printf("Бій між ченцями з енергіями %d і %d. Переможець: %d\n", energy1, energy2, max(energy1, energy2))
}

func max(a, b int) int {
	if a > b {
		return a
	}
	return b
}
