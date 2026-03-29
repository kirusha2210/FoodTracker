const state = {
  dailyGoal: 2200,
  waterGoal: 3000,
  water: 1800,
  meals: [
    {
      name: "Greek yogurt bowl",
      calories: 380,
      time: "08:15",
      notes: "Berries, chia, and almond butter"
    },
    {
      name: "Chicken grain salad",
      calories: 640,
      time: "12:40",
      notes: "Added avocado and extra greens"
    },
    {
      name: "Matcha protein shake",
      calories: 240,
      time: "16:10",
      notes: "Good pre-workout energy"
    }
  ]
};

const summaryGrid = document.querySelector("#summary-grid");
const mealList = document.querySelector("#meal-list");
const mealForm = document.querySelector("#meal-form");
const macroPercent = document.querySelector("#macro-percent");
const calorieProgress = document.querySelector("#calorie-progress");
const waterProgress = document.querySelector("#water-progress");
const focusFormButton = document.querySelector("#focus-form");

const totalCalories = () =>
  state.meals.reduce((sum, meal) => sum + meal.calories, 0);

const completionPercent = () =>
  Math.min(100, Math.round((totalCalories() / state.dailyGoal) * 100));

const summaryItems = () => [
  {
    label: "Calories logged",
    value: `${totalCalories()} kcal`,
    detail: `${state.dailyGoal - totalCalories()} kcal left today`
  },
  {
    label: "Meals captured",
    value: `${state.meals.length}`,
    detail: "A light daily snapshot stays easy to maintain"
  },
  {
    label: "Water intake",
    value: `${state.water} ml`,
    detail: `${state.waterGoal - state.water} ml to hit your hydration goal`
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
            <span>${meal.notes || "No extra notes"}</span>
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

const render = () => {
  renderSummary();
  renderMeals();
};

mealForm.addEventListener("submit", (event) => {
  event.preventDefault();

  const formData = new FormData(mealForm);
  const name = String(formData.get("mealName") || "").trim();
  const calories = Number(formData.get("calories"));
  const time = String(formData.get("time") || "").trim();
  const notes = String(formData.get("notes") || "").trim();

  if (!name || !Number.isFinite(calories) || !time) {
    return;
  }

  state.meals.push({ name, calories, time, notes });
  mealForm.reset();
  document.querySelector("#meal-time").value = currentTimeValue();
  render();
});

const currentTimeValue = () => {
  const now = new Date();
  return `${String(now.getHours()).padStart(2, "0")}:${String(now.getMinutes()).padStart(2, "0")}`;
};

focusFormButton.addEventListener("click", () => {
  document.querySelector("#meal-name").focus();
});

document.querySelector("#meal-time").value = currentTimeValue();
render();
