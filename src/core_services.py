from datetime import datetime, timedelta
from typing import List, Optional
from decimal import Decimal

from src.core_domain_models import WorkOrder


class ProductionPlanningService:
    def schedule_work_order(self, work_order: WorkOrder, resources: List[Resource]) -> bool:
        """
        Schedule a work order based on resource availability and capacity constraints.
        Returns True if scheduling was successful, False otherwise.
        """
        # Check resource availability
        for resource in resources:
            if not self._check_resource_availability(resource, work_order.start_date, work_order.end_date):
                return False

        # Assign resources to work order
        work_order.assigned_resources = resources
        return True

    def _check_resource_availability(self, resource: Resource, start_date: datetime, end_date: datetime) -> bool:
        current_date = start_date
        while current_date <= end_date:
            if not resource.availability_schedule.get(current_date, False):
                return False
            current_date += timedelta(hours=1)
        return True

class InventoryManagementService:
    def check_material_availability(self, bom: BillOfMaterials, quantity: int) -> bool:
        """
        Check if there are sufficient materials available for production.
        """
        for material, required_qty in bom.components.items():
            if material.stock_quantity < (required_qty * quantity):
                return False
        return True

    def reserve_materials(self, bom: BillOfMaterials, quantity: int) -> None:
        """
        Reserve materials for a work order.
        """
        for material, required_qty in bom.components.items():
            material.stock_quantity -= (required_qty * quantity)

    def release_materials(self, bom: BillOfMaterials, quantity: int) -> None:
        """
        Release reserved materials if work order is cancelled.
        """
        for material, required_qty in bom.components.items():
            material.stock_quantity += (required_qty * quantity)

class WorkflowManagementService:
    def create_workflow_instance(self, workflow: Workflow, project: Project) -> None:
        """
        Create a new workflow instance for a project.
        """
        project.assigned_workflow = workflow
        self._initialize_workflow_steps(workflow)

    def _initialize_workflow_steps(self, workflow: Workflow) -> None:
        """
        Initialize workflow steps and calculate critical path.
        """
        # Implementation of critical path calculation
        pass

    def update_step_status(self, workflow: Workflow, step_id: str, completed: bool) -> None:
        """
        Update the status of a workflow step and manage dependencies.
        """
        # Implementation of step status update
        pass

class ProjectManagementService:
    def calculate_project_metrics(self, project: Project) -> Dict:
        """
        Calculate key project metrics including costs, progress, and schedule variance.
        """
        metrics = {
            'total_cost': self._calculate_total_cost(project),
            'progress_percentage': self._calculate_progress(project),
            'schedule_variance': self._calculate_schedule_variance(project)
        }
        return metrics

    def _calculate_total_cost(self, project: Project) -> Decimal:
        """
        Calculate total project cost including labor, materials, and expenses.
        """
        labor_cost = sum(
            entry.end_time.hour - entry.start_time.hour
            for entry in project.time_entries
        ) * Decimal('100.00')  # Assuming $100/hour rate

        material_cost = sum(
            sum(material.unit_cost * usage
                for material, usage in work_order.actual_material_usage.items())
            for work_order in project.work_orders
        )

        return labor_cost + material_cost

    def _calculate_progress(self, project: Project) -> float:
        """
        Calculate project progress as percentage.
        """
        completed_steps = sum(1 for step in project.assigned_workflow.steps
                            if step.id in project.completed_step_ids)
        return (completed_steps / len(project.assigned_workflow.steps)) * 100

    def _calculate_schedule_variance(self, project: Project) -> int:
        """
        Calculate schedule variance in days.
        """
        planned_duration = (project.end_date - project.start_date).days
        actual_duration = (datetime.now() - project.start_date).days
        return planned_duration - actual_duration

class TimeAndExpenseService:
    def log_time_entry(self, entry: TimeEntry) -> None:
        """
        Log a time entry for a resource.
        """
        # Implementation of time entry logging
        pass

    def log_expense_entry(self, entry: ExpenseEntry) -> None:
        """
        Log an expense entry for a project.
        """
        # Implementation of expense entry logging
        pass

    def generate_time_report(self, project_id: str, start_date: datetime, end_date: datetime) -> List[TimeEntry]:
        """
        Generate a time report for a specific project and date range.
        """
        # Implementation of time report generation
        pass