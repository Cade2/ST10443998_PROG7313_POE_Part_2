package za.co.emeris.st10443998.group_5_prog7313_poe_part_1.ui.budget

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.R
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.adapters.CategoryAdapter
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.databinding.BottomSheetCategoryBinding
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.databinding.FragmentCategoriesBinding
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.model.Category

class CategoriesFragment : Fragment() {

    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCategoryList()

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.fabAddCategory.setOnClickListener {
            showCategoryBottomSheet(null)
        }
    }

    private fun setupCategoryList() {
        val categories = listOf(
            Category(1, "Groceries", 0, Color.parseColor("#2E7D32")),
            Category(2, "Transport", 0, Color.parseColor("#1565C0")),
            Category(3, "Entertainment", 0, Color.parseColor("#6A1B9A")),
            Category(4, "Utilities", 0, Color.parseColor("#E65100")),
            Category(5, "Dining", 0, Color.parseColor("#C62828"))
        )

        binding.rvCategories.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = CategoryAdapter(categories) { category ->
                showCategoryBottomSheet(category)
            }
        }
    }

    private fun showCategoryBottomSheet(category: Category?) {
        val dialog = BottomSheetDialog(requireContext())
        val sheetBinding = BottomSheetCategoryBinding.inflate(layoutInflater)

        if (category != null) {
            sheetBinding.tvTitle.text = getString(R.string.title_edit_category)
            sheetBinding.etCategoryName.setText(category.name)
        }

        sheetBinding.btnCancel.setOnClickListener { dialog.dismiss() }
        sheetBinding.btnSave.setOnClickListener {
            val name = sheetBinding.etCategoryName.text?.toString()?.trim() ?: ""
            if (name.isEmpty()) {
                sheetBinding.tilCategoryName.error = getString(R.string.error_required_field)
            } else {
                Toast.makeText(requireContext(), getString(R.string.toast_saved), Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        }

        dialog.setContentView(sheetBinding.root)
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
