const state = {
  dailyGoal: 2200,
  waterGoal: 3000,
  water: 1800,
  meals: [],
  foods: [],
  selectedFoodId: null,
  foodApiAvailable: false
};

const defaultFoods = [
  {
    id: "food-greek-yogurt",
    name: "Greek yogurt bowl",
    calories: 380,
    protein: 28,
    fat: 12,
    carbs: 34,
    water: 0,
    serving: "1 bowl",
    notes: "Berries, chia, and almond butter"
  },
  {
    id: "food-chicken-salad",
    name: "Chicken grain salad",
    calories: 640,
    protein: 44,
    fat: 22,
    carbs: 58,
    water: 0,
    serving: "1 plate",
    notes: "Added avocado and extra greens"
  },
  {
    id: "food-matcha-shake",
    name: "Matcha protein shake",
    calories: 240,
    protein: 30,
    fat: 6,
    carbs: 18,
    water: 400,
    serving: "1 shaker",
    notes: "Good pre-workout energy"
  },
  {
    id: "food-salmon-bowl",
    name: "Salmon rice bowl",
    calories: 520,
    protein: 36,
    fat: 18,
    carbs: 42,
    water: 0,
    serving: "1 bowl",
    notes: "Fast dinner default"
  }
];

const summaryGrid = document.querySelector("#summary-grid");
const mealList = document.querySelector("#meal-list");
const mealForm = document.querySelector("#meal-form");
const macroPercent = document.querySelector("#macro-percent");
const calorieProgress = document.querySelector("#calorie-progress");
const waterProgress = document.querySelector("#water-progress");
const focusFormButton = document.querySelector("#focus-form");
const openFoodLibraryButton = document.querySelector("#open-food-library");
const closeFoodLibraryButton = document.querySelector("#close-food-library");
const foodLibrary = document.querySelector("#food-library");
const foodLibraryOverlay = document.querySelector("#food-library-overlay");
const foodLibraryList = document.querySelector("#food-library-list");
const foodSearchInput = document.querySelector("#food-search");
const foodTemplateForm = document.querySelector("#food-template-form");
const mealNameInput = document.querySelector("#meal-name");
const mealCaloriesInput = document.querySelector("#meal-calories");
const mealTimeInput = document.querySelector("#meal-time");
const mealNotesInput = document.querySelector("#meal-notes");

const normalizeFood = (food) => ({
  id: food.id ?? `food-${Date.now()}-${Math.random().toString(16).slice(2)}`,
  name: String(food.name ?? "").trim(),
  calories: Number(food.calories ?? 0),
  protein: Number(food.protein ?? 0),
  fat: Number(food.fat ?? 0),
  carbs: Number(food.carbs ?? 0),
  water: Number(food.water ?? 0),
  serving: String(food.serving ?? "1 serving").trim(),
  notes: String(food.notes ?? "").trim()
});

const normalizeMeal = (meal) => ({
  id: meal.id,
  name: meal.name,
  calories: Number(meal.calories ?? 0),
  protein: Number(meal.protein ?? 0),
  fat: Number(meal.fat ?? 0),
  carbs: Number(meal.carbs ?? 0),
  time: String(meal.eatenAt ?? "").slice(11, 16) || currentTimeValue(),
  notes: meal.notes ?? ""
});

const currentTimeValue = () => {
  const now = new Date();
  return `${String(now.getHours()).padStart(2, "0")}:${String(now.getMinutes()).padStart(2, "0")}`;
};

const totalCalories = () =>
  state.meals.reduce((sum, meal) => sum + meal.calories, 0);

const totalProtein = () =>
  state.meals.reduce((sum, meal) => sum + meal.protein, 0);

const totalFat = () =>
  state.meals.reduce((sum, meal) => sum + meal.fat, 0);

const totalCarbs = () =>
  state.meals.reduce((sum, meal) => sum + meal.carbs, 0);

const completionPercent = () =>
  Math.min(100, Math.round((totalCalories() / state.dailyGoal) * 100));

const filteredFoods = () => {
  const query = foodSearchInput.value.trim().toLowerCase();

  if (!query) {
    return state.foods;
  }

  return state.foods.filter((food) =>
    [food.name, food.notes, food.serving].some((value) =>
      String(value).toLowerCase().includes(query)
    )
  );
};

const summaryItems = () => [
  {
    label: "Calories logged",
    value: `${totalCalories()} kcal`,
    detail: `${Math.max(state.dailyGoal - totalCalories(), 0)} kcal left today`
  },
  {
    label: "Meals captured",
    value: `${state.meals.length}`,
    detail: "Use saved dishes for faster logging during the day"
  },
  {
    label: "Protein / Fat / Carbs",
    value: `${totalProtein()}g / ${totalFat()}g / ${totalCarbs()}g`,
    detail: "Macro balance stays visible while you log meals"
  }
];

const renderSummary = () => {
  summaryGrid.innerHTML = summaryItems()
    .map(
      (item) => `
        <article class="card summary-card">
          <p class="summary-label">${item.label}</p>
          <strong>${item.value}</strong>
          <span>${item.detail}</span>
        </article>
      `
    )
    .join("");

  macroPercent.textContent = `${completionPercent()}%`;
  calorieProgress.value = totalCalories();
  waterProgress.value = state.water;
};

const renderMeals = () => {
  const meals = [...state.meals].sort((left, right) => left.time.localeCompare(right.time));

  mealList.innerHTML = meals
    .map(
      (meal) => `
        <li class="meal-item">
          <div>
            <p>${meal.name}</p>
            <span>${meal.notes || "No extra notes"} · ${meal.protein}P / ${meal.fat}F / ${meal.carbs}C</span>
          </div>
          <div class="meal-meta">
            <strong>${meal.calories} kcal</strong>
            <span>${meal.time}</span>
          </div>
        </li>
      `
    )
    .join("");
};

const renderFoodLibrary = () => {
  const foods = filteredFoods();

  if (!foods.length) {
    foodLibraryList.innerHTML = `
      <article class="food-card empty-card">
        <p>No matching foods yet.</p>
        <span>Try a different search or add your own custom food below.</span>
      </article>
    `;
    return;
  }

  foodLibraryList.innerHTML = foods
    .map(
      (food) => `
        <button class="food-card ${state.selectedFoodId === food.id ? "selected" : ""}" type="button" data-food-id="${food.id}">
          <div class="food-card-top">
            <strong>${food.name}</strong>
            <span>${food.calories} kcal</span>
          </div>
          <p>${food.serving}</p>
          <span>${food.protein}P · ${food.fat}F · ${food.carbs}C${food.water ? ` · ${food.water}ml water` : ""}</span>
        </button>
      `
    )
    .join("");
};

const render = () => {
  renderSummary();
  renderMeals();
  renderFoodLibrary();
};

const openFoodLibrary = (open = true) => {
  foodLibrary.classList.toggle("hidden", !open);
  foodLibraryOverlay.classList.toggle("hidden", !open);
  foodLibrary.setAttribute("aria-hidden", String(!open));
  document.body.classList.toggle("library-open", open);

  if (open) {
    foodSearchInput.focus();
  }
};

const applyFoodToForm = (food) => {
  state.selectedFoodId = food.id;
  mealNameInput.value = food.name;
  mealCaloriesInput.value = food.calories;
  mealNotesInput.value = food.notes;
  openFoodLibrary(false);
  renderFoodLibrary();
};

const loadFoods = async () => {
  try {
    const response = await fetch("/foods");

    if (!response.ok) {
      throw new Error("Food API unavailable");
    }

    const foods = await response.json();
    state.foodApiAvailable = true;
    state.foods = foods.map(normalizeFood);
  } catch (error) {
    state.foodApiAvailable = false;
    state.foods = defaultFoods.map(normalizeFood);
  }
};

const loadMeals = async () => {
  const response = await fetch("/meals");

  if (!response.ok) {
    console.error("Failed to load meals");
    return;
  }

  const meals = await response.json();
  state.meals = meals.map(normalizeMeal);
};

const createMeal = async (payload) => {
  const response = await fetch("/meals", {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify(payload)
  });

  if (!response.ok) {
    throw new Error("Failed to create meal");
  }
};

const createFood = async (payload) => {
  if (state.foodApiAvailable) {
    const response = await fetch("/foods", {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(payload)
    });

    if (!response.ok) {
      throw new Error("Failed to create food");
    }

    const createdFood = await response.json();
    state.foods.unshift(normalizeFood(createdFood));
    return;
  }

  state.foods.unshift(normalizeFood(payload));
};

mealForm.addEventListener("submit", async (event) => {
  event.preventDefault();

  const formData = new FormData(mealForm);
  const name = String(formData.get("mealName") || "").trim();
  const calories = Number(formData.get("calories"));
  const time = String(formData.get("time") || "").trim();
  const notes = String(formData.get("notes") || "").trim();

  if (!name || !Number.isFinite(calories) || !time) {
    return;
  }

  const selectedFood = state.foods.find((food) => food.id === state.selectedFoodId);
  const now = new Date();
  const eatenAt = `${now.toISOString().slice(0, 10)}T${time}:00`;

  try {
    await createMeal({
      name,
      calories,
      eatenAt,
      notes: notes || null,
      protein: selectedFood?.protein ?? 0,
      fat: selectedFood?.fat ?? 0,
      carbs: selectedFood?.carbs ?? 0
    });

    mealForm.reset();
    state.selectedFoodId = null;
    mealTimeInput.value = currentTimeValue();
    await loadMeals();
    render();
  } catch (error) {
    console.error(error);
  }
});

foodTemplateForm.addEventListener("submit", async (event) => {
  event.preventDefault();

  const formData = new FormData(foodTemplateForm);
  const payload = {
    name: String(formData.get("foodName") || "").trim(),
    calories: Number(formData.get("calories")),
    protein: Number(formData.get("protein")),
    fat: Number(formData.get("fat")),
    carbs: Number(formData.get("carbs")),
    water: Number(formData.get("water") || 0),
    serving: String(formData.get("serving") || "").trim(),
    notes: String(formData.get("notes") || "").trim()
  };

  if (
    !payload.name ||
    !payload.serving ||
    [payload.calories, payload.protein, payload.fat, payload.carbs].some((value) => !Number.isFinite(value))
  ) {
    return;
  }

  try {
    await createFood(payload);
    foodTemplateForm.reset();
    renderFoodLibrary();
  } catch (error) {
    console.error(error);
  }
});

foodLibraryList.addEventListener("click", (event) => {
  const trigger = event.target.closest("[data-food-id]");

  if (!trigger) {
    return;
  }

  const food = state.foods.find((item) => item.id === trigger.dataset.foodId);

  if (food) {
    applyFoodToForm(food);
  }
});

foodSearchInput.addEventListener("input", renderFoodLibrary);
openFoodLibraryButton.addEventListener("click", () => openFoodLibrary(true));
closeFoodLibraryButton.addEventListener("click", () => openFoodLibrary(false));
foodLibraryOverlay.addEventListener("click", () => openFoodLibrary(false));

focusFormButton.addEventListener("click", () => {
  mealNameInput.focus();
});

document.addEventListener("keydown", (event) => {
  if (event.key === "Escape") {
    openFoodLibrary(false);
  }
});

const init = async () => {
  mealTimeInput.value = currentTimeValue();
  await Promise.all([loadMeals(), loadFoods()]);
  render();
};

init();
